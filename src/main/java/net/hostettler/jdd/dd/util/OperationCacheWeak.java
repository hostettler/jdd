package net.hostettler.jdd.dd.util;

import java.util.Map;

import com.google.common.collect.MapMaker;

public class OperationCacheWeak<ParameterType, ResultType> {
	private long hits = 0L;

	private long cacheHits = 0L;

	private Map<ParameterType, ResultType> mCache = new MapMaker().concurrencyLevel(4)
			.makeMap();
	
	public void put(ParameterType parameter, ResultType value) {
		this.mCache.put(parameter, value);
	}

	public ResultType get(ParameterType parameter) {
		this.hits++;
		ResultType result = this.mCache.get(parameter);
		if (result != null) {
			this.cacheHits++;
		}
		return result;
	}

	public long getHits() {
		return this.hits;
	}

	public long getCacheHits() {
		return this.cacheHits;
	}

	public long getOpInCache() {
		return this.mCache.size();
	}

	public void clean() {
		this.mCache.clear();
	}

	public void cleanStatistics() {
		this.cacheHits = 0L;
		this.hits = 0L;
	}
}
