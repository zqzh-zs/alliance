
package com.neu.alliance.entity;

import java.io.Serializable;
import java.util.List;

public class CollectionDetailVO implements Serializable {

    private CourseCollection collection;
    private List<Course> coursesPage;

    public CollectionDetailVO() {}

    public CollectionDetailVO(CourseCollection collection, List<Course> coursesPage) {
        this.collection = collection;
        this.coursesPage = coursesPage;
    }

    public CourseCollection getCollection() {
        return collection;
    }

    public void setCollection(CourseCollection collection) {
        this.collection = collection;
    }

    public List<Course> getCoursesPage() {
        return coursesPage;
    }

    public void setCoursesPage(List<Course> coursesPage) {
        this.coursesPage = coursesPage;
    }
}
