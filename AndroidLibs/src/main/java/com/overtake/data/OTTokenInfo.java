package com.overtake.data;

import java.util.HashMap;
import android.annotation.SuppressLint;
import com.overtake.utils.Utils;

@SuppressLint("DefaultLocale")
public class OTTokenInfo {

	public String accessToken;
	public String refreshToken;
	public String scope;
	public long expires;
	public String loginId;
	public String account;

	public OTTokenInfo() {
	}

	public static OTTokenInfo createToken(HashMap<String, String> info) {
		// null| 不包含 | 包含且 null
		if (info == null 
				|| info.containsKey("access_token") == false 
				|| info.get("access_token") == null
				|| info.get("access_token").equals("null")) {
			if (info == null) {
				info = new HashMap<String, String>();
			}

			info.put("access_token", "");
			info.put("refresh_token", "");
			info.put("expires_in", "0");
			info.put("scope", "");
			info.put("loginid", "");
			info.put("account", "");
		}

		return new OTTokenInfo(info);
	}

	public OTTokenInfo(String accessToken, String refreshToken, long expires) {

		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.expires = expires;
	}

	public OTTokenInfo(HashMap<String, String> info) {
		this(info.get("access_token"), info.get("refresh_token"), Long.parseLong(info.get("expires_in")));
		this.scope = info.get("scope");
		this.loginId = info.get("loginid");
		this.account = info.get("account");

	}

	public boolean isValid() {

		return !Utils.isNullOrEmpty(this.accessToken) && !Utils.isNullOrEmpty(this.refreshToken);
	}

	public String toString() {

		return String.format("accessToken = %s, refreshToken = %s, expires = %d", this.accessToken, this.refreshToken, this.expires);
	}
}