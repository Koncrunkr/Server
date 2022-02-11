package ru.comgrid.server.model;

import com.google.gson.JsonObject;

/**
 * Interface indicating that this object can be converted to json.
 */
public interface Jsonable{
    /**
     * Get json representation of this object
     * Must not throw exception
     */
    JsonObject toJson();

    /**
     * Get string representation of json of this object
     * Must not throw exception
     */
    @Override
    String toString();
}
