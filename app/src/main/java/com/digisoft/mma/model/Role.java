package com.digisoft.mma.model;

import java.util.List;

/*********For user profile **********/
public class Role{
    public String roleId;
    public String roleName;
    public String moduleNames;
    public List<Function> functions;
    public Object searchText;

    public Role(String roleId, String roleName, String moduleNames, List<Function> functions, Object searchText) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.moduleNames = moduleNames;
        this.functions = functions;
        this.searchText = searchText;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getModuleNames() {
        return moduleNames;
    }

    public void setModuleNames(String moduleNames) {
        this.moduleNames = moduleNames;
    }

    public List<Function> getFunctions() {
        return functions;
    }

    public void setFunctions(List<Function> functions) {
        this.functions = functions;
    }

    public Object getSearchText() {
        return searchText;
    }

    public void setSearchText(Object searchText) {
        this.searchText = searchText;
    }
}
