package com.digisoft.mma.util;

public class AppConstant {

    public static final String BASE_URL = "https://oiyryh245h.execute-api.ap-southeast-1.amazonaws.com/dev/";

    public static final String TIME_SERVER = "sg.pool.ntp.org";

    public static final String BEARER = "Bearer ";
    public static final String PREVENTATIVE = "PREVENTATIVE";
    public static final String PM = "PM";
    public static final String CORRECTIVE = "CORRECTIVE";
    public static final String CM = "CM";
    public static final String ALL = "ALL";
    public static final String WSCH = "WSCH";
    public static final String INPRG = "INPRG";
    public static final String ACK = "ACK";
    public static final String APPR = "APPR";
    public static final String JOBDONE = "JOBDONE";
    public static final String WEEKLY = "WEEKLY";
    public static final String MONTHLY = "MONTHLY";
    public static final String QUARTERLY = "QUARTERLY";
    public static final String YEARLY = "YEARLY";

    public static final String INVALID_GRANT = "invalid_grant";
    public static final String INVALID_GRANT_MSG = "Login ID or Password is incorrect!";
    public static final String ACCOUNT_LOCK = "Account LOCKED";
    public static final String ACCOUNT_LOCK_MSG = "Your account is locked due to failed login attempts. Please, contact system adminstrator";
    public static final String VERIFICATION_FAIL_MSG = "The operation can't be completed because fault still exists. Please check your faulty part again.";

    public static final long user_inactivity_time = 5 * 60 * 1_000L; //5 minutes for user inactivity

    public static final String THIRD_PARTY_COMMENT_UPDATE = "THIRD_PARTY_COMMENT_UPDATE";
    public static final String COMMENT = "comment";
    public static final String PART_REPLACEMENT_UPDATE = "PART_REPLACEMENT_UPDATE";
    public static final String FAULT_PART_CODE = "faultPartCode";
    public static final String REPLACEMENT_PART_CODE = "replacementPartCode";
    public static final String SERVICE_ORDER_UPDATE = "SERVICE_ORDER_UPDATE";
    public static final String REPORTED_PROBLEM = "reportedProblem";
    public static final String ACTUAL_PROBLEM = "actualProblem";
    public static final String CAUSE = "cause";
    public static final String REMEDY = "remedy";
    public static final String PM_FAULT_FOUND_UPDATE = "PM_FAULT_FOUND_UPDATE";


    public static final String TELCO_UPDATE = "TELCO_UPDATE";
    public static final String POWER_GRIP_UPDATE = "POWER_GRIP_UPDATE";
    public static final String OTHER_CONTRACTOR_UPDATE = "OTHER_CONTRACTOR_UPDATE";

    public static final String TELCO = "TELCO";
    public static final String POWER_GRIP = "POWER_GRIP";
    public static final String OTHER_CONTRACTOR = "OTHER_CONTRACTOR";
    public static final String NO_TYPE = "NO_TYPE";

    public static final String THIRD_PARTY_NUMBER= "thirdPartyNumber";
    public static final String DOCKET_NUMBER = "docketNumber";
    public static final String CT_PERSONNEL= "ctPersonnel";
    public static final String REFER_DATE = "referDate";
    public static final String EXPECTED_COMPLETION_DATE = "expectedCompletionDate";
    public static final String CLEARANCE_DATE = "clearanceDate";
    public static final String FAULT_STATUS = "faultStatus";
    public static final String OFFICER = "officer";
    public static final String FAULT_DETECTED_DATE = "faultDetectedDate";
    public static final String ACTION_DATE = "actionDate";
    public static final String ACTION_TAKEN = "actionTaken";
    public static final String REMARKS_ON_FAULT = "remarkOnFault";
    public static final String COMPANY_NAME = "companyName";
    public static final String CONTACT_NUMBER = "contactNumber";
    public static final String PM_CHECK_LIST_DONE = "PM_CHECK_LIST_DONE";
    public static final String PM_CHECK_LIST_REMARK = "PM_CHECK_LIST_REMARK";

    public static final String YES = "yes";
    public static final String NO = "no";

    public static final String CM_Step_ONE = "CM_STEP_ONE";
    public static final String CM_Step_TWO = "CM_STEP_TWO";
    public static final String CM_Step_THREE = "CM_STEP_THREE";
    public static final String PM_Step_ONE = "PM_STEP_ONE";
    public static final String PM_Step_TWO = "PM_STEP_TWO";

    public static final String PRE_BUCKET_NAME = "pids-pre-maintenance-photo";
    public static final String POST_BUCKET_NAME = "pids-post-maintenance-photo";

    public static final String UPLOAD_ERROR = "UPLOAD_ERROR";
    public static final String FAILURE = "ON FAILURE";

}