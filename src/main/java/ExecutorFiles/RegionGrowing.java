package ExecutorFiles;

import ParserFiles.BoundsSubclause;
import ParserFiles.QueryEnums;
import ParserFiles.QuerySpecifics;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.google.common.graph.*;

import static ExecutorFiles.SeedSelection.createImageOfState;

public class RegionGrowing
{
    // seeds and valid area => make neighborhood graph
    public static boolean doPolygonsOverlap(Polygon poly1, Polygon poly2) {
        for (int i = 0; i < poly1.npoints; i++) {
            for (int j = 0; j < poly2.npoints; j++) {
                if (doLineSegmentsIntersect(
                        poly1.xpoints[i], poly1.ypoints[i],
                        poly1.xpoints[(i + 1) % poly1.npoints], poly1.ypoints[(i + 1) % poly1.npoints],
                        poly2.xpoints[j], poly2.ypoints[j],
                        poly2.xpoints[(j + 1) % poly2.npoints], poly2.ypoints[(j + 1) % poly2.npoints])) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean doLineSegmentsIntersect(
            int x1, int y1, int x2, int y2,
            int x3, int y3, int x4, int y4) {
        int dx12 = x2 - x1;
        int dy12 = y2 - y1;
        int dx34 = x4 - x3;
        int dy34 = y4 - y3;

        int denominator = (dy12 * dx34 - dx12 * dy34);

        if (denominator == 0) {
            return false; // lines are parallel or coincident
        }

        int t1 =
                ((x1 - x3) * dy34 + (y3 - y1) * dx34)
                        / denominator;
        int t2 =
                ((x3 - x1) * dy12 + (y1 - y3) * dx12)
                        / (-denominator);

        return (t1 >= 0 && t1 <= 1 && t2 >= 0 && t2 <= 1);
    }
    public static MutableGraph<Area> createGraphFromAreas(List<Area> filteredAreas) {
        MutableGraph<Area> graph = GraphBuilder.undirected().build();

        //create the nodes
        for (Area area: filteredAreas) {
            graph.addNode(area);
        }
        //create the edges in the graph
        for (int i = 0; i < filteredAreas.size(); i++) {
            for (int j = i + 1; j < filteredAreas.size(); j++) {
                Area area1 = filteredAreas.get(i);
                Area area2 = filteredAreas.get(j);

                if (!graph.hasEdgeConnecting(area1, area2) && doPolygonsOverlap(area1.getPolygon(), area2.getPolygon())) {
                    graph.putEdge(area1, area2);
                }
            }
        }

        return graph;
    }
    // Main method for region growing
    public static List<Area> regionGrowing(Set<Area> seeds, List<Area> filteredRegions,QuerySpecifics queryInfo) {
        List<Area> validRegions = new ArrayList<>();
        MutableGraph<Area> regionsGraph = createGraphFromAreas(filteredRegions);
        for (Area seed : seeds) {
            Set<Area> currentRegion = new HashSet<>();
            currentRegion.add(seed);
            boolean regionIsValid = false;

            while (!regionIsValid) {
                List<Area> validNeighbors = new ArrayList<>();
                Area bestNeighbor = null;
                double minObjectiveValue = Double.MAX_VALUE;
                int i = 0;
                // Loop over all neighbors of the current region
                for (Area adjacentArea : regionsGraph.adjacentNodes(seed)) {
                    // Temporarily add the neighbor to the region to test constraints
                    currentRegion.add(adjacentArea);

                    if (maintainsConstraints(currentRegion, queryInfo)) {
                        validNeighbors.add(adjacentArea);
                        createImageOfState(currentRegion, "currentRegion" + i, "Testing: " );
                        i++;
                        // Calculate the objective function value for the current region
                        double objectiveValue = calculateObjectiveFunction(currentRegion, queryInfo);

                        // Select the neighbor that minimizes the objective function
                        if (objectiveValue < minObjectiveValue) {
                            minObjectiveValue = objectiveValue;
                            bestNeighbor = adjacentArea;
                        }
                    }
                    // Remove the neighbor after testing
                    currentRegion.remove(adjacentArea);
                }

                // If no valid neighbors can be found, stop growing the region
                if (bestNeighbor == null) {
                    break;
                }

                // Add the best neighbor to the region
                currentRegion.add(bestNeighbor);

                // Check if the region now satisfies the constraints
                if (maintainsConstraints(currentRegion, queryInfo)) {
                    regionIsValid = true;
                }
            }

            // Handle regions that do not meet the criteria
            if (!maintainsConstraints(currentRegion, queryInfo)) {
//                System.out.println(validRegions);
                if (queryInfo.getPValueEnum() == QueryEnums.pType.PMAX) {
                    filteredRegions.remove(seed);
                } else {
                    // Handle other cases as necessary
                }
            } else {
                // Add the valid region to the list of valid regions
                validRegions.addAll(currentRegion);
            }
        }

        return validRegions;
    }

    //Below code by John

//    public static List<Area> regionGrowing(Set<Area> seeds, List<Area> filteredRegions, MutableGraph<Area> regionsGraph, QuerySpecifics queryInfo) {
//
//        //TODO: how do i ensure i minimized the objective function also how do i know who my region neighbors are
//            //loop over all of the region neughbors and choose the one valid neighbor who maintains constraint and has the min value for the objective function
//        //keep adding areas to region until AVG constraint is met ( need to see if adding the neighbor would increase the avg count etc.)
//
//        for (Area seed: seeds) {
//
//            List<Area> regionsToCompare = new ArrayList<>();
//            Set<Area> currentRegion = new HashSet<>();
//            currentRegion.add(seed);
//            //loop over all the neighbors of this current node
//            for (Area adjacentArea: regionsGraph.adjacentNodes(seed)) {
//
//
//                //the second that our region is valid we should stop growing the region
//                if (maintainsConstraints(currentRegion, queryInfo)) {
//                    break;
//                }
//                else  {
//                    //grow region to the minimum objective function
//                    //aka loop over all the neighbors return a list of them that are valid and then for each
//                    //one of those valid neighbors return the minimum of the
//
//
//                }
//
//            }
//            //aka we exhausted all the possibilties ad could not meet the constraints
//            if (!maintainsConstraints(currentRegion, queryInfo)) {
//                //TODO: handle logic for region not meeting criteria
//                //if we are using pMAX ensure we get rid of the area if we are not going to be able to
//                if (queryInfo.getPValueEnum() == QueryEnums.pType.PMAX) {
//                    filteredRegions.remove(seed);
//                }
//            }
//
//
//        }
//
//        return null;
//    }

    // Method to calculate the objective function value for a region
    public static double calculateObjectiveFunction(Set<Area> currentRegion, QuerySpecifics queryInfo) {
        double objectiveValue = 0.0;

        // Assuming we're minimizing the sum of spatially extensive attributes as the objective
        for (Area area : currentRegion) {
            objectiveValue += area.getSpatiallyExtensiveAttribute();
        }

        // Depending on the specific objective, you may want to incorporate other factors
        return objectiveValue;
    }

    //Takes input of a set containing a bunch of areas and will return boolean true if it meets all the constraints specified
    public static boolean maintainsConstraints(Set<Area> neighborhood, QuerySpecifics queryInfo) {

        //loop over all the AGG functions and check them
        for (BoundsSubclause constraint: queryInfo.getBoundsSubclauses()) {

            double valOverExtensiveAttributes = 0;
            switch (constraint.getAggFunction()) {

                case SUM:
                    for (Area area: neighborhood) {
                        valOverExtensiveAttributes += area.getSpatiallyExtensiveAttribute();
                    }
                    break;
                case MIN:
                    for (Area area: neighborhood) {
                        valOverExtensiveAttributes = Math.min(valOverExtensiveAttributes,area.getSpatiallyExtensiveAttribute());
                    }

//                //TODO: do i even need to handle these since we test min max constraint for all the areas in seed selection
//                    break;
//                case MAX:
//
//                    break;
                case COUNT:
                    valOverExtensiveAttributes = neighborhood.size();
                    break;
                case AVG:
                    for (Area area: neighborhood) {
                        valOverExtensiveAttributes += area.getSpatiallyExtensiveAttribute();
                    }
                    valOverExtensiveAttributes = valOverExtensiveAttributes / neighborhood.size();
                    break;
                default:

                    break;
            }
            System.out.println("Extensive = " + valOverExtensiveAttributes);
            if (constraint.getComparisonOperator1() != null) {
                if (!compare(constraint.getLowerBound(), valOverExtensiveAttributes, constraint.getComparisonOperator1().trim())) {
                    //current set does not meet the constraints as specified by the query information
                    System.out.println("False1");
                    return false;
                }
            }

            if (constraint.getComparisonOperator2() != null) {
                if (!compare(valOverExtensiveAttributes, constraint.getUpperBound(), constraint.getComparisonOperator2().trim())) {
                    System.out.println("False2");
                    return false;
                }
            }




        }

        return true;
    }

    public static boolean compare(double operand1, double operand2, String operator) {
        System.out.println( + operand1 + " "+ operator + " " + operand2 );
        if (operator == null) {
            // If the comparison operator is null, return true, assuming no constraint is applied
            return true;
        }
        return switch (operator) {
            case "<" -> operand1 < operand2;
            case "<=" -> operand1 <= operand2;
            case ">" -> operand1 > operand2;
            case ">=" -> operand1 >= operand2;
            default -> throw new IllegalArgumentException("Invalid operator: " + operator);
        };
    }

}
