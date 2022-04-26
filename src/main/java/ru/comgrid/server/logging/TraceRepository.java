package ru.comgrid.server.logging;

import java.util.Collection;
import java.util.List;

public interface TraceRepository<T>{
	Collection<T> findAll();

	void add(T trace);
}
