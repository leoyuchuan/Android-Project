package com.ossimulator.offloadingos.params;

public class TaskParams {
	private double c1, c2, c3, s2, ce, cd;
	private double T;
	private int jobs;
	private int remainingJobs;
	private double releasedTime;
	private double startTime; // in nanosec
	private int intervalIndex;

	public TaskParams() {
		c1 = 0; // in ms
		c2 = 0; // in ms
		c3 = 0; // in ms
		s2 = 0; // in ms
		ce = 0; // in ms
		cd = 0; // in ms
		T = 0; // in ms
		jobs = 0;
		remainingJobs = 0;
	}

	public double getC1() {
		return c1;
	}

	public void setC1(double c1) {
		this.c1 = c1;
	}

	public double getC2() {
		return c2;
	}

	public void setC2(double c2) {
		this.c2 = c2;
	}

	public double getC3() {
		return c3;
	}

	public void setC3(double c3) {
		this.c3 = c3;
	}

	public double getS2() {
		return s2;
	}

	public void setS2(double s2) {
		this.s2 = s2;
	}

	public double getCe() {
		return ce;
	}

	public void setCe(double ce) {
		this.ce = ce;
	}

	public double getCd() {
		return cd;
	}

	public void setCd(double cd) {
		this.cd = cd;
	}

	public double getT() {
		return T;
	}

	public void setT(double t) {
		T = t;
	}

	public double getJobs() {
		return jobs;
	}

	public void setJobs(int jobs) {
		this.jobs = jobs;
	}

	public int getRemainingJobs() {
		return remainingJobs;
	}

	public void setRemainingJobs(int remainingJobs) {
		this.remainingJobs = remainingJobs;
	}

	public double getReleasedTime() {
		return releasedTime;
	}

	public void setReleasedTime(double releasedTime) {
		this.releasedTime = releasedTime;
	}

	public double getStartTime() {
		return startTime;
	}

	public void setStartTime(double startTime) {
		this.startTime = startTime;
	}

	public int getIntervalIndex() {
		return intervalIndex;
	}

	public void setIntervalIndex(int intervalIndex) {
		this.intervalIndex = intervalIndex;
	}

	public static TaskParams getDefaultParams() {
		TaskParams tp = new TaskParams();
		tp.setC1(10);
		tp.setC2(10);
		tp.setC3(10);
		tp.setCd(0);
		tp.setCe(0);
		tp.setIntervalIndex(1);
		tp.setJobs(10);
		tp.setRemainingJobs(10);
		tp.setS2(5);
		tp.setT(1000);
		return tp;
	}
}
