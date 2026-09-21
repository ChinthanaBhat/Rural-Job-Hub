package com.android.project.utility;
public class Constants {
    public static final String APP_DESCRIPTION = " This app is mainly intended for rural labourers and Rozgar Sewaks of a Gram Panchayat. " +
            "The app helps in the process of procuring employment for labourers." +
            "It overcomes all the drawbacks of the existing system and provides a paperless solution for all the issues faced. \n" +
            "this application is used to automate this entire process and to prevent any corruption that " +
            "takes place under this scheme. The app can be used for paperless registration of job demands under MGNREGA. The app enables electronic submission of demand \n" +
            "Thus the entire process of registration, issue of job card, demand for work is handled using this mobile application.";

    public static final String MISSING_NAME = "Please enter name";
    public static final String MISSING_PASSWORD = "Please enter your password";
    public static final String INVALID_ADMIN_CREDENTIALS = "Wrong Credentials";
    public static final String INVALID_USER = "Wrong Credentials";
    public static final String MISSING_MAIL = "Please enter mail";
    public static final String MISSING_PASSWORD_CONFIRMATION = "Please confirm your password";
    public static final String PASSWORD_MISMATCH = "Password does not match";
    public static final String INVALID_MOBILE = "Mobile number should be 10 digit number";
    public static final String DETAILS_SUCESS = "Details added sucessfully";
    public static final String NO_FIELDINSPECTOR_DELETE = "No One FieldInspector is Available to be delet";

    public static final String MISSING_MOBILE ="Please enter your Mbl no" ;
    public static final String MISSING_USERNAME ="Please enter username" ;
    public static final String INVALID_EMAIL = "Please enter valid Email";
    public static final String MISSING_EMAIL="Please enter your  Mail";

    // Database version
    public static final int DATABASE_VERSION = 1;

    // Database name
    public static final String DATABASE_NAME = "KayakaBandhu";


    // Common column names
    public static final String ID_KEY = "_id";
    public static final String LABOURER_TABLE_NAME = "labourers";
    public static final String LABOURER_NAME_COLUMN = "name";
    public static final String LABOURER_USERNAME_COLUMN = "username";
    public static final String LABOURER_PASSWORD_COLUMN = "password";
    public static final String LABOURER_MOBILE_COLUMN = "mobile";
    public static final String LABOURER_AGE_COLUMN = "age";
    public static final String LABOURER_GENDER_COLUMN = "gender";
    public static final String LABOURER_PROFILEPATH_COLUMN = "profilepath";
    public static final String LABOURER_ISAPPROVED_COLUMN = "isapproved";
    public static final String LABOURER_IS_JOBCARD_ISSUED_COLUMN = "is_jobcard_issued";
    public static final String LABOURER_JOBCARD_QR_CODE_COLUMN = "qr_code";
    public static final String LABOURER_AADHAR_COLUMN = "aadhar";
    public static final String LABOURER_BPL_CARD_COLUMN = "bpl_card";
    public static final String LABOURER_VOTER_ID_COLUMN = "voter_id";
    public static final String LABOURER_IS_REGISTERED_FOR_SCHEME_COLUMN = "is_registered";
    public static final String LABOURER_PANCHAYATH_COLUMN = "panchayath";
    public static final String LABOURER_ALLOTTED_FOR_JOB_COLUMN = "allotted_for_job";
    public static final String LABOURER_BANK_COLUMN = "bank";
    public static final String LABOURER_IFSC_COLUMN = "ifsc";
    public static final String LABOURER_ACCOUNT_COLUMN = "account";

    public static final String ROZGARSEWAK_TABLE_NAME ="rozgarsewaks" ;
    public static final String ROZGARSEWAK_NAME_KEY = "Name";
    public static final String ROZGARSEWAK_CONTACT_KEY = "Contact";
    public static final String ROZGARSEWAK_PROFILEPATH_KEY = "ProfilePath";
    public static final String ROZGARSEWAK_USERNAME_KEY ="Username" ;
    public static final String ROZGARSEWAK_PASSWORD_KEY ="Password" ;
    public static final String ROZGARSEWAK_PANCHAYATH_KEY ="Panchayath" ;


    public static final String JOB_TABLE_NAME ="Jobs" ;
    public static final String JOB_TITLE_KEY = "Title";
    public static final String JOB_DESCRIPTION_KEY = "Description";
    public static final String JOB_DURATION_KEY = "Duration";
    public static final String JOB_NUMBER_OF_LABOURERS ="Number_Of_Labourers" ;
    public static final String JOB_FUNDS_ALLOTTED ="Funds_Allotted" ;

    // Attendance Table
    public static final String ATTENDANCE_TABLE_NAME = "Attendance";
    public static final String ATTENDANCE_DATE_KEY = "Date";
    public static final String LABOURER_ID_KEY = "LabourerID";
    public static final String JOB_ID_KEY = "JobID";

    public static final String JOB_LABOURER_TABLE_NAME = "JobLabourer";


    public static final String FORGOT_PASSWORD ="The registered password has been sent to your registered mobile number" ;

    public static final String ADMIN_PASSWORD ="admin" ;
    public static final String ADMIN_PASSWORD_KEY ="password" ;
    public static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    public static final int REQUEST_CAMERA =123 ;
    public static final int SELECT_FILE =234 ;

    public static final String MISSING_PROFILEPHOTO = "Please select a profile photo";
    public static final String DUPLICATE_USERNAME = "Username already exists. Please select a different username";

    public static final String SEWAK_ADDED_SUCCESSFULLY = "Rozgar Sewak is added successfully";
    public static final String LABOURER_APPROVED_SUCCESSFULLY = "Labourer is approved successfully";
    public static final String DRAWABLE_RESOURCE = "drawable";

    public static final String NO_LABOURER_WITH_NAME = "Labourer with specified name does not exist";
    public static final String NO_REGISTERED_LABOURERS_DESCRIPTION = "There are no registered labourers";
    public static final String MISSING_TITLE = "Enter job title";
    public static final String MISSING_DESCRIPTION = "Please enter a description";
    public static final String MISSING_NUMBER_OF_LABOURERS = "Please enter number of labourers required for the job";
    public static final String MISSING_DURATION = "Please enter the duration of the job";
    public static final String MISSING_FUNDS = "Please enter funds allotted";
    public static final String JOB_ADDED_SUCCESSFULLY = "Job is added successfully";
    public static final String NO_APPROVED_LABOURERS_DESCRIPTION = "There are no approved labourers";
    public static final String JOBCARD_ISSUED_SUCCESSFULLY = "Job Card is issued successfully";
    public static final String QR_CODE_GENERATED_SUCCESSFULLY = "QR Code is generated successfully";

    public static final int MY_CAMERA_REQUEST_CODE = 143;

    public static final String FEEDBACK_SUBJECT = "Kayaka Bandhu App";
    public static final String FEEDBACK_MAILID = "aks77429@gmail.com";
    public static final String HELP_MESSAGE = "Please mail to \naks77429@gmail.com\nfor any help regarding the app.";

    public static final String MISSING_PANCHAYATH = "Please enter the Gram Panchayath";
    public static final String LABOURER_ADDED_SUCCESSFULLY = "Labourer is added successfully";
    public static final String LABOURER_REGISTRATION_SUCCESSFULL = "Registration is successful";
    public static final String MISSING_AMOUNT = "Please enter amount";
    public static final String MISSING_DATE = "Select a date";
    public static final String NO_LABOURER_DESCRIPTION = "None of the labourers are allotted for this job";
    public static final String NO_LABOURER_AVAILABLE_DESCRIPTION = "There are no labourers";

    public static final String ATTENDANCE_MARKED_SUCCESSFULLY = "Attendance is marked successfully";
    public static final String MISSING_OLD_PASSWORD = "Please enter your old password";
    public static final String OLD_PASSWORD_INCORRECT = "Your old password is incorrect";
    public static final String MISSING_NEW_PASSWORD = "Please enter your new password";
    public static final String PASSWORD_CHANGED_SUCCESSFULLY = "Password is changed successfully";
    public static final int MINIMUM_PASSWORD_LENGTH = 8;
    public static final String INVALID_PASSWORD ="Invalid password. It should contain minimum 8 characters with a digit and a special character" ;

    public static final String NO_JOBS = "There are no jobs";
    public static final String MISSING_BPLCARD = "Please enter BPL Card number";
    public static final String MISSING_AADHARCARD = "Please enter Aadhar Card number";
    public static final String MISSING_VOTERID = "Please enter Voter ID";

    public static final String LABOURER_REGISTERED_SUCCESSFULLY = "Labourer is registered successfully for the scheme";
    public static final String JOB_WILL_BE_ALLOTTED_SHORTLY = "Job will be allotted shortly";
    public static final String JOB_CARD_NOT_ISSUED = "Job Card is not yet issued";
    public static final String DID_NOT_AGREE_TO_TERMS = "You can demand for job only after agreeing to terms and conditions";



    public static final String MALE = "Male";
    public static final String FEMALE = "Female";

    public static final String NO_LABOURERS_REGISTERED_FOR_SCHEME_DESCRIPTION = "None of the labourers have registered for the scheme";
    public static final String INVALID_VOTERID = "Voter id should contain 10 characters. Please enter a valid voter id";
    public static final String INVALID_AADHARCARD = "Aadhar id should be 12 digits.  Please enter a valid aadhar id";
    public static final String ALREADY_REGISTERED_FOR_SCHEME = "You have already registered for the scheme";
    public static final String NOT_REGISTERED_FOR_SCHEME = "Please register for the scheme before demanding the job";
    public static final String LABOURERS_ALLOTTED_SUCCESSFULLY = "Labourers are allotted successfully";
    public static final String JOB_CARD_IS_ALREADY_ISSUED = "Job Card is already issued for this labourer";
    public static final String PAYMENT_MADE_SUCCESSFULLY = "Payment made successfully";
    public static final String NO_JOBS_DESCRIPTION = "Panchayath Pradhan has not added any jobs";
    public static final String NO_ATTENDANCE_DESCRIPTION = "There is no attendance";
    public static final String MISSING_BANK = "Enter bank name";
    public static final String MISSING_IFSC = "Enter IFSC Code";
    public static final String MISSING_ACCOUNT = "Enter Account Number";

    public static final String MISSING_BEFOREPHOTO = "Please select a before photo";
    public static final String MISSING_AFTERPHOTO = "Please select a after photo";
    public static final String PROGRESSUPLOADING = "Uploading is in progress. Please wait...";
    public static final int REQUEST_CAMERA_FOR_BEFORE_PHOTO =321 ;
    public static final int REQUEST_CAMERA_FOR_AFTER_PHOTO =213 ;
    public static final int SELECT_FILE_FOR_BEFORE_PHOTO =432 ;
    public static final int SELECT_FILE_FOR_AFTER_PHOTO =423 ;
    public static final int BEFORE =1 ;
    public static final int AFTER =2 ;
    public static final String PHOTOS_UPLOADED_SUCCESSFULLY = "Photos are uploaded successfully";
    public static final String BEFORE_PHOTO_KEY = "Before_Photo";
    public static final String AFTER_PHOTO_KEY = "After_Photo";
    public static final String DATE_KEY = "Date";

    public static final String QR_CODE_GENERATION_NOT_POSSIBLE_TILL_SCHEME_REGISTRATION = "QR Code can be generated only after registering for the scheme";

    public static final String DUPLICATE_VOTERID = "Voter ID already exists. Please enter a different voter id";
    public static final String DUPLICATE_AADHARID = "Aadhar ID already exists. Please enter a different aadhar id";
    public static final String DUPLICATE_BPLCARD = "BPL Card Number already exists. Please enter a different bpl card number";
    public static final String NO_WORK_PROGRESS = "There is no work progress updates for this job";
    public static final String NO_ISSUES_FEEDBACK = "There are no issues or feedbacks for this job";



}
