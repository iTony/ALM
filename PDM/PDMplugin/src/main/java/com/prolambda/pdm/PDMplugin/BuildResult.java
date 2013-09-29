package com.prolambda.pdm.PDMplugin;

import hudson.model.Result;
import hudson.model.AbstractBuild;

public enum BuildResult {
	 	SUCCESS,
	    FAILURE,
	    UNSTABLE,
	    ABORTED,
	    NOT_BUILT,
	    FIXED;
	   
	    @SuppressWarnings("rawtypes")
		public static BuildResult fromBuild(AbstractBuild build) {
	        if (build.getResult().equals(Result.SUCCESS)) {
	            Result prevResult = build.getPreviousBuild() != null ? build.getPreviousBuild().getResult() : null;
	            if (Result.FAILURE.equals(prevResult) || Result.UNSTABLE.equals(prevResult)) {
	                return FIXED;
	            }
	            return SUCCESS;
	        } else if (build.getResult().equals(Result.UNSTABLE)) {
	            return UNSTABLE;
	        } else if (build.getResult().equals(Result.ABORTED)) {
	            return ABORTED;
	        } else if (build.getResult().equals(Result.NOT_BUILT)) {
	            return NOT_BUILT;
	        } else {
	            return FAILURE;
	        }
	    }
}
