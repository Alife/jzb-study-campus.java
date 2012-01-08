/**
 * 
 */
package com.jzb.ipa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import com.jzb.tpoi.data.BaseEntityComparationType;
import com.jzb.tpoi.data.BaseEntityCompatator;
import com.jzb.tpoi.data.TBaseEntity;
import com.jzb.tpoi.data.TCategory;
import com.jzb.tpoi.data.TMap;
import com.jzb.tpoi.data.TMapElement;
import com.jzb.tpoi.data.TPoint;

/**
 * @author n63636
 * 
 */
public class ViewModelUtil {

    public static ArrayList<TMapElement> getFlatContent(TMap map, ArrayList<TCategory> categories) {

        ArrayList<TMapElement> list = new ArrayList<TMapElement>();

        if (categories == null || categories.size() == 0) {
            for (TCategory cat : map.getAllCategories()) {
                cat.setDisplayCount(cat.getAllPoints().size());
                list.add(cat);
            }
            list.addAll(map.getAllPoints());
        } else {
            Collection<TPoint> points1 = _getFilteredPointsByCategories(map, categories);
            list.addAll(points1);
            // list.addAll(categories.get(categories.size() - 1).getAllRecursivePoints());
        }

        return list;
    }

    public static ArrayList<TMapElement> getHierarchicalContent(TMap map, ArrayList<TCategory> categories) {

        Collection<TPoint> points1 = _getFilteredPointsByCategories(map, categories);
        HashSet<TCategory> cats1 = _getPointsCategories(map, points1, categories);
        ArrayList<TCategory> cats2 = _filterSubcategories(cats1);
        ArrayList<TPoint> points2 = _filterCategorizedPoints(points1, cats2);

        // Hay que añadir las subcategorias de la ultima categoria??
        ArrayList<TMapElement> list = new ArrayList<TMapElement>();
        list.addAll(cats2);
        list.addAll(points2);

        return list;
    }

    public static void shortCollection(ArrayList<? extends TBaseEntity> items, BaseEntityComparationType compType) {

        BaseEntityCompatator comparator = new BaseEntityCompatator(compType);
        Collections.sort(items, comparator);
    }

    private static ArrayList<TPoint> _filterCategorizedPoints(Collection<TPoint> points, Collection<TCategory> categories) {

        ArrayList<TPoint> list = new ArrayList<TPoint>();

        for (TPoint p : points) {

            String pointID = p.getId();
            boolean contained = false;

            for (TCategory cat : categories) {
                if (cat.containsPointById(pointID, true)) {
                    cat.incrementDisplayCount();
                    contained = true;
                }
            }

            if (!contained) {
                list.add(p);
            }

        }

        return list;
    }

    private static ArrayList<TCategory> _filterSubcategories(Collection<TCategory> categories) {

        ArrayList<TCategory> rootCategories = new ArrayList<TCategory>();
        for (TCategory c1 : categories) {

            String catID = c1.getId();
            boolean contained = false;
            for (TCategory c2 : categories) {
                if (c2.containsCategoryById(catID, true)) {
                    contained = true;
                    break;
                }
            }

            if (!contained) {
                // Como sera mostrada le pone el contador a cero
                c1.setDisplayCount(0);
                rootCategories.add(c1);
            }
        }

        return rootCategories;
    }

    private static Collection<TPoint> _getFilteredPointsByCategories(TMap map, ArrayList<TCategory> categories) {

        if (categories == null || categories.size() == 0) {
            return map.getAllPoints();
        }

        if (categories.size() == 1) {
            return categories.get(0).getAllRecursivePoints();
        } else {

            HashSet<TPoint> filteredPoints = new HashSet<TPoint>(categories.get(0).getAllRecursivePoints());
            for (int n = 1; n < categories.size(); n++) {
                filteredPoints.retainAll(categories.get(n).getAllRecursivePoints());
            }

            return filteredPoints;
        }

    }

    private static HashSet<TCategory> _getPointsCategories(TMap map, Collection<TPoint> points, Collection<TCategory> excludedCategories) {

        HashSet<TCategory> categories = new HashSet<TCategory>();

        for (TPoint p : points) {
            String pointID = p.getId();
            for (TCategory cat : map.getAllCategories()) {

                if (excludedCategories != null && excludedCategories.contains(cat)) {
                    continue;
                }

                if (cat.containsPointById(pointID)) {
                    categories.add(cat);
                }

            }
        }

        return categories;
    }

}
