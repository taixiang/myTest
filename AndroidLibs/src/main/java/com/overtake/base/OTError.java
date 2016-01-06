package com.overtake.base;

@SuppressWarnings("serial")
public final class OTError extends Error {
	
	private String _domain;
	private int _code;
	
	public OTError(String domain, String errorString, int code) {
		
		super(errorString);
		_domain = domain;
		_code = code;
	}
	
	public OTError(String domain, String errorString) {
		
		super(errorString);
		_domain = domain;
	}
	
	public String getDomain() {
		
		return _domain;
	}
	
	public int getCode() {
		
		return _code;
	}
}
