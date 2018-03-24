package com.sinha.google.bean;

import java.io.Serializable;

public final class EmailCount implements Comparable<EmailCount>, Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -23498988783893L;
	
	final String email;
	final int count;
	

	public EmailCount(String email, int count) {
		super();
		this.email = email;
		this.count = count;
	}

	public String getEmail() {
		return email;
	}

	public int getCount() {
		return count;
	}

	@Override
	public int compareTo(EmailCount obj) {
		EmailCount em=(EmailCount)obj;  
		if(count==em.count)  
		return 0;  
		else if(count<em.count)  
		return 1;  
		else  
		return -1;  
	}

	@Override
	public String toString() {
		return "EmailCount [email=" + email + ", count=" + count + "]";
	}
	

}
