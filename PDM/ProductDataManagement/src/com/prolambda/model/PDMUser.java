package com.prolambda.model;

public class PDMUser {

	private int id;
	private String userId;
	private String name;
	
	public PDMUser(){
		
	}
	
	public PDMUser(String name){
		this.name = name;
	}
	public PDMUser(String userId,String name){
		this.userId = userId;
		this.name = name;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
