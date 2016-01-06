package com.overtake.base;

public class OTConsts {

	public static final int KXDATA_ID_DEFAULT = 0;
	public static final int KXDATA_PAGESIZE_DEFAULT = 20;

	public static final int KXDATA_REQUEST_REFRESH = 0;
	public static final int KXDATA_REQUEST_GETMORE = KXDATA_REQUEST_REFRESH + 1;
	public static final int KXDATA_REQUEST_COMMON_QUERY = KXDATA_REQUEST_GETMORE + 1;
	public static final int KXDATA_REQUEST_POST = KXDATA_REQUEST_COMMON_QUERY + 1;

	public static final int KXDATA_REQUEST_ACCESS_TOKEN = KXDATA_REQUEST_COMMON_QUERY + 1;
	public static final int KXDATA_REQUEST_REFRESH_TOKEN = KXDATA_REQUEST_ACCESS_TOKEN + 1;
	public static final int KXDATA_REQUEST_CHECK_CAPTCHA = KXDATA_REQUEST_REFRESH_TOKEN + 1;
	public static final int KXDATA_REQUEST_CUSTOM_START_INDEX = KXDATA_REQUEST_CHECK_CAPTCHA + 1;

	public static final int DATA_REQUEST_RET_CODE_SUCCESS = 0;
	public static final String DATA_REQUEST = "dataRequest";
	public static final String DATA_REQUEST_KEY_DATA = "data";
	public static final String DATA_REQUEST_KEY_RET = "ret";
	public static final String DATA_REQUEST_KEY_ERRNO = "errno";
	public static final String DATA_REQUEST_KEY_MSG = "msg";
	public static final String DATA_REQUEST_KEY_TOTAL = "total";
	public static final String DATA_REQUEST_KEY_RESULT = "result";
	public static final String DATA_REQUEST_KEY_HASMORE = "has_more";
	public static final String DATA_REQUEST_KEY_TIME = "time";
	public static final String DATA_REQUEST_KEY_LOCALHOST = "localhost";

	public static final String DATA_REQUEST_KEY_CATEGORY = "dataCategory";
	public static final String DATA_REQUEST_KEY_TYPE = "dataRequestType";
	public static final String DATA_REQUEST_KEY_DATAID = "dataId";
	public static final String DATA_REQUEST_KEY_ARGS = "args";
	public static final String DATA_REQUEST_KEY_USERINFO = "userinfo";
	
	public static final String DATA_REQUEST_KEY_UPLOADITEMID = "uploaditemid";
	public static final String DATA_REQUEST_KEY_UPLOADTIME = "uploadtime";
	
	
	public static final int MALE = 0;
	public static final int FEMALE = 1;
	
	public static final int LIST_NUM_PER_PAGE = 20;
	public static final int REQUEST_LATESTDATA_TIME_INTERVAL = 20;
}
