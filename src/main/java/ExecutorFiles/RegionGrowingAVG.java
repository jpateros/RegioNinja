package ExecutorFiles;

import java.awt.*;
import java.util.ArrayList;
import java.util.*;
import java.util.List;
import java.util.Random;

import static ExecutorFiles.PolygonGraph.printPolygons;
import static ExecutorFiles.RegionGrowing.doPolygonsOverlap;

public class RegionGrowingAVG {
    public static void main(String[] args) {

    }

    public static List<Set<Area>> regionGrowingAvg(Set<Area> seedAreas, double low, double high, Set<Area> allAreas, double s) {
        //making assumption that substep 2.1 is already done for min max filtering
        List<Set<Area>> regionListP = null;

        Set<Area> unassigned_low = new HashSet<>();
        Set<Area> unassigned_high = new HashSet<>();
        Set<Area> unassigned_average = new HashSet<>();
        //classify the seeds properly
        for (Area seed : seedAreas) {
            double spatialAttribute = seed.getSpatiallyExtensiveAttribute();
            if (spatialAttribute < low) {
                unassigned_low.add(seed);
            } else if (spatialAttribute > high) {
                unassigned_high.add(seed);
            } else {
                unassigned_average.add(seed);
            }
        }

       // regionListP = unAssignedAreasAlgo(s, low, )




        return regionListP;
    }

    public static List<Set<Area>> unAssignedAreasAlgo(double s, double low, double u, Set<Area> unassigned_low, Set<Area> unassigned_high, Set<Area> allAreas)
    {
        List<Set<Area>> finalRegions = new ArrayList<>();
        Set<Area> u_areas = union(unassigned_low, unassigned_high);
        Set<Area> removed_areas = new HashSet<>();
        boolean updated = false;
        for (Area area : u_areas)
        {
            Set<Area> regionR = new HashSet<>();
            regionR.add(area);
            updated = true;

            Set<Area> neighbor_areas = getNeighbors(regionR, allAreas);
            while (updated)
            {
                updated = false;
                if (low <=  getRegionAverage(regionR) && getRegionAverage(regionR) <= u) {
                    finalRegions.add(regionR);
                    removed_areas.addAll(regionR);
                }
                else {
                    for (Area neighbor : neighbor_areas)
                    {
                        if (!neighbor.getBelongsToRegion())
                        {
                            double regionAvg = getRegionAverage(regionR);
                            if (regionAvg < low && neighbor.getDissimilarityAttribute() > u || (regionAvg > u && neighbor.getDissimilarityAttribute() < low))
                                regionR.add(neighbor);
                                updated = true;
                                break;
                            }
                        }
                    }
                }
            }

        return finalRegions;
    }

    public static double getRegionAverage(Set<Area> regions)
    {
        double sum = 0;
        for (Area current : regions)
        {
            sum += current.getSpatiallyExtensiveAttribute();
        }
        return sum / regions.size();
    }

    public static Set<Area> getNeighbors(Set<Area> interest, Set<Area> totalAreas)
    {
        Set<Area> neighbors = new HashSet<>();
        for (Area regionArea : interest) {
            for (Area testOutsideArea : totalAreas) {
                if (doPolygonsOverlap(regionArea.getPolygon(), testOutsideArea.getPolygon()))
                {
                    neighbors.add(testOutsideArea);
                }
            }

        }
        return neighbors;
    }

    public static <T> Set<T> union(Set<T> set1, Set<T> set2) {

        Set<T> unionSet = new HashSet<>(set1);
        unionSet.addAll(set2);

        return unionSet;
    }


    public static boolean satisifiesConstraint(Area testArea, double low, double high) {
        double attribute = testArea.getSpatiallyExtensiveAttribute();
        return attribute > low && attribute < high;
    }


//    public static List<Area> setupRegions() {
//        List<Area> areaList = new ArrayList<>();
//
//        int cellSize = 50;
//        int[] xCoords1 = {0 * cellSize, 0 * cellSize, 1 * cellSize, 1 * cellSize};
//        int[] yCoords1 = {0 * cellSize, 3 * cellSize, 3 * cellSize, 0 * cellSize};
//
//        int[] xCoords2 = {1 * cellSize, 2 * cellSize, 2 * cellSize, 1 * cellSize};
//        int[] yCoords2 = {0 * cellSize, 0 * cellSize, 1 * cellSize, 1 * cellSize};
//
//        int[] xCoords3 = {2 * cellSize, 3 * cellSize, 3 * cellSize, 2 * cellSize};
//        int[] yCoords3 = {0 * cellSize, 0 * cellSize, 1 * cellSize, 1 * cellSize};
//
//        double[] xCoords4 = {1 * cellSize, 2.5 * cellSize, 3 * cellSize, 3 * cellSize};
//        double[] yCoords4 = {1 * cellSize, 1 * cellSize, 3 * cellSize, 0 * cellSize};
//
//        int[] xCoords5 = {2 * cellSize, 2 * cellSize, 4 * cellSize, 4 * cellSize};
//        int[] yCoords5 = {3 * cellSize, 6 * cellSize, 6 * cellSize, 3 * cellSize};
//
//        int[] xCoords6 = {4 * cellSize, 4 * cellSize, 6 * cellSize, 6 * cellSize};
//        int[] yCoords6 = {3 * cellSize, 6 * cellSize, 6 * cellSize, 3 * cellSize};
//
//        int[] xCoords7 = {3 * cellSize, 3 * cellSize, 5 * cellSize, 5 * cellSize};
//        int[] yCoords7 = {0 * cellSize, 3 * cellSize, 3 * cellSize, 0 * cellSize};
//
//        int[] xCoords8 = {5 * cellSize, 5 * cellSize, 6 * cellSize, 6 * cellSize};
//        int[] yCoords8 = {3 * cellSize, 6 * cellSize, 6 * cellSize, 3 * cellSize};
//
//        Polygon polygon1 = new Polygon(xCoords1, yCoords1, 4);
//        Polygon polygon2 = new Polygon(xCoords2, yCoords2, 4);
//        Polygon polygon3 = new Polygon(xCoords3, yCoords3, 4);
//        Polygon polygon4 = new Polygon(xCoords4, yCoords4, 4);
//        Polygon polygon5 = new Polygon(xCoords5, yCoords5, 4);
//        Polygon polygon6 = new Polygon(xCoords6, yCoords6, 4);
//        Polygon polygon7 = new Polygon(xCoords7, yCoords7, 4);
//        Polygon polygon8 = new Polygon(xCoords8, yCoords8, 4);
//
//        int minValue = 5000;
//        int maxValue = 10000;
//        Random rand = new Random();
//
//        // right now the spatially extensive attribute is the population
//        Area area1 = new Area(1, polygon1, 1, 0.0);
//        Area area2 = new Area(1, polygon2, 1, 0.0);
//        Area area3 = new Area(1, polygon3, 1, 0.0);
//        Area area4 = new Area(1, polygon4, 1, 0.0);
//        Area area5 = new Area(1, polygon5, 1, 0.0);
//        Area area6 = new Area(1, polygon6, 1, 0.0);
//        Area area7 = new Area(1, polygon7, 1, 0.0);
//        Area area8 = new Area(1, polygon8, 1, 0.0);
//
//        areaList.add(area1);
//        areaList.add(area2);
//        areaList.add(area3);
//        areaList.add(area4);
//        areaList.add(area5);
//        areaList.add(area6);
//        areaList.add(area7);
//        areaList.add(area8);
//
//        printPolygons(areaList, "testingThisB" + ".png", "yep");
//
//        return areaList;
//    }
//
//}

}