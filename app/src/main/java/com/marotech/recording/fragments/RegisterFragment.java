package com.marotech.recording.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.marotech.recording.R;
import com.marotech.recording.api.HttpCode;
import com.marotech.recording.api.RegisterRequest;
import com.marotech.recording.api.ResponseType;
import com.marotech.recording.api.ServiceResponse;
import com.marotech.recording.callbacks.RemoteServiceCallback;
import com.marotech.recording.model.Constants;
import com.marotech.recording.service.RegDataService;
import com.marotech.recording.service.RegistrationService;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class RegisterFragment extends BaseFragment implements RemoteServiceCallback {

    public String DATE_OF_BIRTH;
    private LocalDate dob;
    private String country;
    private int regMinAge;
    private TextView regAnswer1Label = null;
    private TextView regAnswer2Label = null;
    private TextView regAnswer3Label = null;

    public RegisterFragment(Context context) {
        super(context);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        clearSession();
        DATE_OF_BIRTH = getString(R.string.date_of_birth);
        final View root = inflater.inflate(R.layout.fragment_register, container, false);
        regAnswer1Label = root.findViewById(R.id.regAnswer1Label);
        regAnswer2Label = root.findViewById(R.id.regAnswer2Label);
        regAnswer3Label = root.findViewById(R.id.regAnswer3Label);
        country = context.getString(R.string.regCountry);
        regMinAge = Integer.valueOf(context.getString(R.string.regMinAge));

        //---------------------------------------------------

        Spinner spinner = root.findViewById(R.id.regSpinner);
        final RegisterFragment fragment = this;

        setUpButtons(root, spinner, fragment);

        return root;
    }

    void updateSecurityQuestions(ServiceResponse response) {

        String secQuestion1 = (String) response.getAdditionalInfo().get(Constants.SEC_QUESTION_1);
        String secQuestion2 = (String) response.getAdditionalInfo().get(Constants.SEC_QUESTION_2);
        String secQuestion3 = (String) response.getAdditionalInfo().get(Constants.SEC_QUESTION_3);

        regAnswer1Label.setText(secQuestion1);
        regAnswer2Label.setText(secQuestion2);
        regAnswer3Label.setText(secQuestion3);
    }

    private void setUpButtons(final View root, final Spinner spinner, final RegisterFragment fragment) {
        final EditText regEmail = root.findViewById(R.id.regEmail);
        final EditText regPassword = root.findViewById(R.id.regPassword);
        final EditText regFirstName = root.findViewById(R.id.regFirstName);
        final EditText regMiddleName = root.findViewById(R.id.regMiddleName);
        final EditText regLastName = root.findViewById(R.id.regLastName);
        final EditText regNationalId = root.findViewById(R.id.regNationalId);
        final EditText regAddress = root.findViewById(R.id.regAddress);
        final EditText regTown = root.findViewById(R.id.regTown);
        final EditText regMobileNumber = root.findViewById(R.id.regMobileNumber);
        final TextView regDateOfBirth = root.findViewById(R.id.regDateOfBirth);
        final EditText regAnswer1 = root.findViewById(R.id.regAnswer1);
        final EditText regAnswer2 = root.findViewById(R.id.regAnswer2);
        final EditText regAnswer3 = root.findViewById(R.id.regAnswer3);

        final TextView regEmailLabel = root.findViewById(R.id.regEmailLabel);
        final TextView regPasswordLabel = root.findViewById(R.id.regPasswordLabel);
        final TextView regFirstNameLabel = root.findViewById(R.id.regFirstNameLabel);
        final TextView regLastNameLabel = root.findViewById(R.id.regLastNameLabel);
        final TextView regNationalIdLabel = root.findViewById(R.id.regNationalIdLabel);
        final TextView regAddressLabel = root.findViewById(R.id.regAddressLabel);
        final TextView regTownLabel = root.findViewById(R.id.regTownLabel);
        final TextView regMobileNumberLabel = root.findViewById(R.id.regMobileNumberLabel);

        Button saveButton = root.findViewById(R.id.regBtnSave);
        Button clearButton = root.findViewById(R.id.regBtnClear);

        regDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(regDateOfBirth);
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                regEmail.setText("");
                regPassword.setText("");
                regFirstName.setText("");
                regMiddleName.setText("");
                regLastName.setText("");
                regNationalId.setText("");
                regAddress.setText("");
                regTown.setText("");
                regDateOfBirth.setText("");
                regAnswer1.setText("");
                regAnswer2.setText("");
                regAnswer3.setText("");
                regMobileNumber.setText("");
                spinner.setSelection(0);

            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                StringBuilder message = new StringBuilder();
                boolean validated = validate(regEmail, regEmailLabel, "email", message);

                if (!validate(regPassword, regPasswordLabel, "password", message)) {
                    validated = false;
                }
                if (!validate(regFirstName, regFirstNameLabel, "email", message)) {
                    validated = false;
                }
                if (!validate(regLastName, regLastNameLabel, "fFirstName", message)) {
                    validated = false;
                }
                if (!validate(regNationalId, regNationalIdLabel, "nationalId", message)) {
                    validated = false;
                }
                if (!validate(regAddress, regAddressLabel, "address", message)) {
                    validated = false;
                }
                if (!validate(regTown, regTownLabel, "town", message)) {
                    validated = false;
                }
                if (!validate(regAnswer1, regAnswer1Label, "answer1", message)) {
                    validated = false;
                }
                if (!validate(regAnswer2, regAnswer2Label, "answer2", message)) {
                    validated = false;
                }
                if (!validate(regAnswer3, regAnswer3Label, "answer3", message)) {
                    validated = false;
                }
                if (!validate(regMobileNumber, regMobileNumberLabel, "email", message)) {
                    validated = false;
                }

                if (!validated) {
                    Log.e(TAG, message.toString());
                }
                if (regDateOfBirth.getText().equals(DATE_OF_BIRTH)) {
                    validated = false;
                    regDateOfBirth.setBackgroundColor(Color.parseColor("#E9967A"));
                } else {
                    regDateOfBirth.setBackgroundColor(Color.WHITE);
                }

                if (dob != null) {
                    if (dob.isAfter(LocalDate.now().minusYears(regMinAge))) {
                        validated = false;
                        regDateOfBirth.setText(getString(R.string.dobGreater) + " > " + regMinAge);
                        regDateOfBirth.setBackgroundColor(Color.parseColor("#E9967A"));
                    } else {
                        regDateOfBirth.setBackgroundColor(Color.WHITE);
                    }
                }

                if (validated) {
                    RegistrationService service = new RegistrationService(fragment);
                    RegisterRequest request = new RegisterRequest();
                    request.setFirstName(regFirstName.getText().toString());
                    if (regMiddleName.getText() != null) {
                        request.setMiddleName(regMiddleName.getText().toString());
                    }
                    request.setLastName(regLastName.getText().toString());
                    request.setEmail(regEmail.getText().toString());
                    request.setMobileNumber(regMobileNumber.getText().toString());
                    request.setAddress(regAddress.getText().toString());
                    request.setTown(regTown.getText().toString());
                    request.setCountry(country);
                    service.register(request);
                }
            }
        });
    }

    private void showDatePickerDialog(final TextView targetView) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(selectedYear, selectedMonth, selectedDay);
                        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
                        String formattedDate = sdf.format(calendar.getTime());
                        targetView.setText(getString(R.string.dob) + ": " + formattedDate);

                        dob = calendar.getTime().toInstant()  // Date to Instant
                                .atZone(ZoneId.systemDefault())  // Instant to ZonedDateTime
                                .toLocalDate();
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    @Override
    public void onObjectsFetched(ServiceResponse serviceResponse) {

        if (serviceResponse.getCode() != HttpCode.OK) {
            sendToStatusPage("Error doing registration: " + serviceResponse.getMessage());
            return;
        }
        ResponseType responseType = serviceResponse.getResponseType();

        if (ResponseType.REG_RESPONSE == responseType) {
            sendToStatusPage(serviceResponse.getMessage());
        }
    }

    private final String TAG = "recorder_RegisterFragment";
}
