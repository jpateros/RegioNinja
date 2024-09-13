package ExecutorFiles;

import ParserFiles.InvalidRSqlSyntaxException;
import ParserFiles.Parser;
import ParserFiles.QuerySpecifics;
import com.google.common.graph.MutableGraph;

import java.util.List;
import java.util.Set;

import static ExecutorFiles.SeedSelection.*;
import static ExecutorFiles.RegionGrowing.*;

public class Executor {

    public static void main(String[] args) throws InvalidQueryInformation {
        List<Area> areaList = createGridAreas();

        Parser parseQuery = new Parser();

        String query = " SELECT REGIONS, REGIONS.p;"
                + "FROM NYC_census_tracts;"
                + "WHERE p=5 ,"
                + "6000 <= AVG ON income, 0 <= MIN ON population, OBJECTIVE COMPACT,"
                + "OPTIMIZATION CONNECTED, HEURISTIC TABU;";


        QuerySpecifics queryInfo = null;
        try {
            parseQuery.validateQuery(query);
            queryInfo = parseQuery.getQueryInfo();
            System.out.println(queryInfo.toString());
        } catch (InvalidRSqlSyntaxException e) {
            System.out.println(e);
        }
//Assume pmax will return one seed at random
        Set<Area> seedSet = SeedSelection(areaList, (int) queryInfo.getPValueDouble(), 50, true, parseQuery.getQueryInfo(), true);
        System.out.println(areaList);
        System.out.println(areaList.size());

        List<Area> regionGrowing = regionGrowing(seedSet,areaList,queryInfo);
        System.out.println(regionGrowing);
        System.out.println(regionGrowing.size());
        // MutableGraph<Area> graphOfValidAreas = createGraphFromAreas(dummyAreas);
    }
}