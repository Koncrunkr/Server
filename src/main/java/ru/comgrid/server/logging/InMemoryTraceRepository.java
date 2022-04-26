package ru.comgrid.server.logging;

import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayDeque;
import java.util.Collection;

public class InMemoryTraceRepository<T> implements TraceRepository<T>{
	private final int capacity;
	private final ArrayDeque<T> traces;

	public InMemoryTraceRepository(@Value("${ru.comgrid.trace.max-count}") int capacity){
		this.capacity = capacity;
		this.traces = new ArrayDeque<>(capacity);
	}

	@Override
	public Collection<T> findAll(){
		synchronized(this.traces){
			return traces.clone();
		}
	}

	@Override
	public void add(T trace){
		synchronized(this.traces){
			if(traces.size() == capacity){
				traces.poll();
			}
			traces.addLast(trace);
		}
	}
}
