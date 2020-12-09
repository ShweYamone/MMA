package com.freelance.solutionhub.mma.model;

/*********For user profile **********/
public class Function{
    public String functionCode;
    public String functionName;
    public String moduleName;

    public Function(String functionCode, String functionName, String moduleName) {
        this.functionCode = functionCode;
        this.functionName = functionName;
        this.moduleName = moduleName;
    }

    public String getFunctionCode() {
        return functionCode;
    }

    public void setFunctionCode(String functionCode) {
        this.functionCode = functionCode;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }
}
