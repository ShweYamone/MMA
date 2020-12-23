package com.freelance.solutionhub.mma.util;

import java.security.PublicKey;

import retrofit2.http.PUT;

public class AppConstant {

    public static final String BASE_URL = "https://oiyryh245h.execute-api.ap-southeast-1.amazonaws.com/dev/";

    public static final String PM = "PREVENTATIVE";
    public static final String pm = "PM";
    public static final String CM = "CORRECTIVE";
    public static final String cm = "CM";
    public static final String ALL = "ALL";
    public static final String WSCH = "WSCH";
    public static final String INPRG = "INPRG";
    public static final String ACK = "ACK";
    public static final String APPR = "APPR";
    public static final String JOBDONE = "JOBDONE";
    public static final long user_inactivity_time = 5 * 60 * 1000; //5 minutes for user inactivity

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


    public static final String TELCO_UPDATE = "TELCO_UPDATE";
    public static final String POWER_GRIP_UPDATE = "POWER_GRIP_UPDATE";
    public static final String OTHER_CONTRACTOR_UPDATE = "OTHER_CONTRACTOR_UPDATE";

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


    public static final String YES = "yes";
    public static final String NO = "no";

    public static final String CM_Step_ONE = "CM_STEP_ONE";
    public static final String CM_Step_TWO = "CM_STEP_TWO";
    public static final String CM_Step_THREE = "CM_STEP_THREE";
    public static final String PM_Step_ONE = "PM_STEP_ONE";

    public static final String PRE_BUCKET_NAME = "pids-pre-maintenance-photo";
    public static final String POST_BUCKET_NAME = "pids-post-maintenance-photo";

}