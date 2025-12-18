package com.marotech.recording.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.marotech.recording.R;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class MediaFragment extends BaseFragment {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 101;

    // Video recording constants
    private static final String MIME_TYPE = "video/avc";
    private static final int VIDEO_WIDTH = 1280;
    private static final int VIDEO_HEIGHT = 720;
    private static final int BIT_RATE = 2000000;
    private static final int FRAME_RATE = 30;

    private String currentPhotoPath, currentVideoPath;
    private File photoFile, videoFile;
    private boolean isRecordingVideo = false;

    private CameraDevice cameraDevice;
    private CameraCaptureSession captureSession;
    private TextureView textureView;
    private HandlerThread backgroundThread;
    private Handler backgroundHandler;

    // Video recording components
    private MediaCodec videoEncoder;
    private Surface inputSurface;
    private MediaMuxer mediaMuxer;
    private int videoTrackIndex = -1;
    private StreamingThread recordingThread;
    private Button captureImageButton;
    private Button recordVideoButton;
    private MediaFormat videoFormat;
    private final BufferInfo bufferInfo = new BufferInfo();

    // Server endpoint
    private final String streamUrl = "http://your-server.com:8080/stream";

    public MediaFragment(Context context) {
        super(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        captureImageButton = view.findViewById(R.id.captureImageButton);
        recordVideoButton = view.findViewById(R.id.recordVideoButton);
        textureView = view.findViewById(R.id.textureView);

        captureImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecordingVideo) {  // Disable photo during video recording
                    checkCameraPermissionAndCapture();
                } else {
                    Toast.makeText(requireContext(), "Stop video recording first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        recordVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecordingVideo) {
                    stopVideoRecording();
                } else {
                    checkPermissionsAndRecord();
                }
            }
        });

        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                openCamera();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            }
        });

        return view;
    }

    private void checkPermissionsAndRecord() {
        if (isRecordingVideo) return;  // Prevent duplicate start

        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, REQUEST_RECORD_AUDIO_PERMISSION);
        } else {
            startVideoRecording();
        }
    }

    private void startVideoRecording() {
        if (isRecordingVideo) return;  // State guard

        try {
            // 1. UPDATE STATE FIRST
            isRecordingVideo = true;
            recordVideoButton.setText("Stop Recording");

            videoFile = createVideoFile();
            currentVideoPath = videoFile.getAbsolutePath();
            mediaMuxer = new MediaMuxer(videoFile.getAbsolutePath(), MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

            setupVideoEncoder();
            startBackgroundThread();
            openCamera();

            recordingThread = new StreamingThread();
            recordingThread.start();

            Toast.makeText(requireContext(), "Recording started + live streaming", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            isRecordingVideo = false;  // Reset on error
            recordVideoButton.setText("Record Video");
            Toast.makeText(requireContext(), "Recording failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void stopVideoRecording() {
        if (!isRecordingVideo) return;  // State guard

        // 1. UPDATE STATE FIRST
        isRecordingVideo = false;
        recordVideoButton.setText("Record Video");

        // 2. Wait for thread to finish muxing final frames
        if (recordingThread != null) {
            recordingThread.interrupt();
            try {
                recordingThread.join(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            recordingThread = null;
        }

        // 3. Drain remaining encoder buffers
        drainEncoderAndFinalize();

        closeCamera();
        stopBackgroundThread();

        Toast.makeText(requireContext(), "Video saved: " + currentVideoPath, Toast.LENGTH_SHORT).show();
        sendVideo(videoFile);  // Auto-share after saving
    }

    private void drainEncoderAndFinalize() {
        if (videoEncoder != null) {
            while (true) {
                int encoderStatus = videoEncoder.dequeueOutputBuffer(bufferInfo, 10000);
                if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    break;
                } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    videoFormat = videoEncoder.getOutputFormat();
                    if (mediaMuxer != null && videoTrackIndex < 0) {
                        videoTrackIndex = mediaMuxer.addTrack(videoFormat);
                        mediaMuxer.start();
                    }
                } else if (encoderStatus >= 0) {
                    ByteBuffer encodedData = videoEncoder.getOutputBuffer(encoderStatus);
                    if (encodedData != null && bufferInfo.size > 0) {
                        if (mediaMuxer != null && videoTrackIndex >= 0) {
                            mediaMuxer.writeSampleData(videoTrackIndex, encodedData, bufferInfo);
                        }
                        sendH264FrameToServer(encodedData, bufferInfo);
                    }
                    videoEncoder.releaseOutputBuffer(encoderStatus, false);
                }
            }

            videoEncoder.stop();
            videoEncoder.release();
            videoEncoder = null;
        }

        if (mediaMuxer != null && videoTrackIndex >= 0) {
            mediaMuxer.stop();
            mediaMuxer.release();
            mediaMuxer = null;
        }
        videoTrackIndex = -1;
    }

    private void openCamera() {
        CameraManager manager = (CameraManager) requireContext().getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                if (characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {
                    // Camera2 components
                    StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    if (map != null) {
                        manager.openCamera(cameraId, stateCallback, backgroundHandler);
                    }
                    break;
                }
            }
        } catch (SecurityException | CameraAccessException e) {
            Toast.makeText(requireContext(), "Cannot access camera", Toast.LENGTH_SHORT).show();
        }
    }

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            cameraDevice = camera;
            startPreview();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            closeCamera();
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            closeCamera();
        }
    };

    private void startPreview() {
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            texture.setDefaultBufferSize(VIDEO_WIDTH, VIDEO_HEIGHT);
            Surface surface = new Surface(texture);

            final CaptureRequest.Builder previewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            previewRequestBuilder.addTarget(surface);
            if (inputSurface != null) {
                previewRequestBuilder.addTarget(inputSurface);
            }

            cameraDevice.createCaptureSession(Arrays.asList(surface, inputSurface),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(CameraCaptureSession session) {
                            captureSession = session;
                            try {
                                previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                                previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);
                                session.setRepeatingRequest(previewRequestBuilder.build(), null, backgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(CameraCaptureSession session) {
                        }
                    }, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void setupVideoEncoder() {
        try {
            videoFormat = MediaFormat.createVideoFormat(MIME_TYPE, VIDEO_WIDTH, VIDEO_HEIGHT);
            videoFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
            videoFormat.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);
            videoFormat.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
            videoFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 2);

            videoEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
            videoEncoder.configure(videoFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            inputSurface = videoEncoder.createInputSurface();
            videoEncoder.start();
        } catch (IOException e) {
            Toast.makeText(requireContext(), "Encoder setup failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void closeCamera() {
        if (captureSession != null) {
            captureSession.close();
            captureSession = null;
        }
        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
    }

    private void startBackgroundThread() {
        backgroundThread = new HandlerThread("CameraBackground");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        if (backgroundThread != null) {
            backgroundThread.quitSafely();
            try {
                backgroundThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            backgroundThread = null;
            backgroundHandler = null;
        }
    }

    // COMPLETE IMAGE CAPTURE METHODS RESTORED
    private void checkCameraPermissionAndCapture() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            captureImage();
        }
    }

    private void captureImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireContext().getPackageManager()) != null) {
            try {
                photoFile = createImageFile();
                Uri photoURI = FileProvider.getUriForFile(requireContext(),
                        requireContext().getPackageName() + ".fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } catch (IOException ex) {
                Toast.makeText(requireContext(), "Error creating image file", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile("JPEG_" + timeStamp + "_", ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private File createVideoFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        File video = File.createTempFile("VID_" + timeStamp + "_", ".mp4", storageDir);
        currentVideoPath = video.getAbsolutePath();
        return video;
    }

    private void sendVideo(File videoFile) {
        Uri uri = FileProvider.getUriForFile(requireContext(),
                requireContext().getPackageName() + ".fileprovider", videoFile);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("video/mp4");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Send Video"));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (!isRecordingVideo) captureImage();
        } else if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION && grantResults.length > 1 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            if (!isRecordingVideo) startVideoRecording();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == -1) {
            Toast.makeText(requireContext(), "Image saved: " + currentPhotoPath, Toast.LENGTH_SHORT).show();
            sendImage(photoFile);
        }
    }

    private void sendImage(File imageFile) {
        Uri uri = FileProvider.getUriForFile(requireContext(),
                requireContext().getPackageName() + ".fileprovider", imageFile);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/jpeg");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Send Image"));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (isRecordingVideo) stopVideoRecording();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isRecordingVideo) stopVideoRecording();
    }

    private class StreamingThread extends Thread {
        @Override
        public void run() {
            while (isRecordingVideo && !isInterrupted()) {
                int outputBufferIndex = videoEncoder.dequeueOutputBuffer(bufferInfo, 10000);
                if (outputBufferIndex >= 0) {
                    ByteBuffer buffer = videoEncoder.getOutputBuffer(outputBufferIndex);

                    if (videoTrackIndex < 0 && videoFormat != null) {
                        videoTrackIndex = mediaMuxer.addTrack(videoFormat);
                        mediaMuxer.start();
                    }

                    if (mediaMuxer != null && videoTrackIndex >= 0 && bufferInfo.size > 0) {
                        mediaMuxer.writeSampleData(videoTrackIndex, buffer, bufferInfo);
                    }

                    if (buffer != null && bufferInfo.size > 0) {
                        sendH264FrameToServer(buffer, bufferInfo);
                    }

                    videoEncoder.releaseOutputBuffer(outputBufferIndex, false);
                }
            }
        }
    }


    private void sendH264FrameToServer(ByteBuffer buffer, BufferInfo info) {
        // TODO: Implement HTTP POST / WebSocket streaming here
    }
}
