package src.main.java.ParserFiles;

import lombok.Data;

import static ParserFiles.QueryEnums.Aggregate;

@Data
public class BoundsSubclause {
//Data class represnting the parsed inforamtion from a single Bounds subclause
    private Double LowerBound;
    private Aggregate AggFunction;
    private Double UpperBound;
    private String UpperBoundAttribute;
    private String comparisonOperator1;
    private String comparisonOperator2;


    public BoundsSubclause() {
        this.LowerBound = null;
        this.AggFunction = null;
        this.UpperBound = null;
        this.UpperBoundAttribute = null;
        this.comparisonOperator1 = null;
        this.comparisonOperator2 = null;
    }
}
