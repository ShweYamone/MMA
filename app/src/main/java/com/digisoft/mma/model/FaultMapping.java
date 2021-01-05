package com.digisoft.mma.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class FaultMapping implements Serializable {
    Map<String, List<Object>> problems;
    Map<String, Object> problemCauseMap;
    Map<String, Object> causeRemedyMap;

    public FaultMapping(Map<String, List<Object>> problems, Map<String, Object> problemCauseMap, Map<String, Object> causeRemedyMap) {
        this.problems = problems;
        this.problemCauseMap = problemCauseMap;
        this.causeRemedyMap = causeRemedyMap;
    }

    public Map<String, List<Object>> getProblems() {
        return problems;
    }

    public void setProblems(Map<String, List<Object>> problems) {
        this.problems = problems;
    }

    public Map<String, Object> getProblemCauseMap() {
        return problemCauseMap;
    }

    public void setProblemCauseMap(Map<String, Object> problemCauseMap) {
        this.problemCauseMap = problemCauseMap;
    }

    public Map<String, Object> getCauseRemedyMap() {
        return causeRemedyMap;
    }

    public void setCauseRemedyMap(Map<String, Object> causeRemedyMap) {
        this.causeRemedyMap = causeRemedyMap;
    }
}
