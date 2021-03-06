package com.prolambda.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class ComponentCategory implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1188788805819802539L;
	private int id;
	private String name;
	private String description;
	private Timestamp created;
	private Timestamp modified;
	private int componentCount;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Timestamp getCreated() {
		return created;
	}
	public void setCreated(Timestamp created) {
		this.created = created;
	}
	public Timestamp getModified() {
		return modified;
	}
	public void setModified(Timestamp modified) {
		this.modified = modified;
	}
	public int getComponentCount() {
		return componentCount;
	}
	public void setComponentCount(int componentCount) {
		this.componentCount = componentCount;
	}
}
