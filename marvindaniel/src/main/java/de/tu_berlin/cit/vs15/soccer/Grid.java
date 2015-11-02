package de.tu_berlin.cit.vs15.soccer;

import java.awt.Point;
import java.awt.geom.Point2D;

public class Grid {
	private int maxX, maxY;
	private int fieldsX, fieldsY;
	double granularityX, granularityY;
	private int[][] grid;

	public Grid(int maxX, int maxY, int fieldsX, int fieldsY) {
		this.maxX = maxX;
		this.maxY = maxY;
		this.granularityX = ((double) (maxX * 2)) / (double) fieldsX;
		this.granularityY = ((double) (maxY * 2)) / (double) fieldsY;
		this.fieldsX = fieldsX;
		this.fieldsY = fieldsY;
		this.grid = new int[fieldsX][fieldsY];
	}

	public void addPosition(int x, int y) {
		int xAbs = Math.abs(x);
		int yAbs = Math.abs(y);

		if (x == maxX)
			x--;
		if (y == maxY)
			y--;

		else if (yAbs > maxY || xAbs > maxX) {
			return;
		}
		grid[(int) ((maxX + x) / granularityX)][(int) ((y + maxY) / granularityY)]++;
	}

	public int[][] getGrid() {
		// int[][] result = new int[grid.length][grid[0].length];

		return this.grid;
	}

	public int getGridCell(int i, int j) { // i = index of xField in Array, j =
											// index of yfield in array, throws
											// Arrayindexoutofbounds exception
		return grid[i][j];
	}

	public int getMaxX() {
		return maxX;
	}

	public int getMaxY() {
		return maxY;
	}

	public int getFieldsX() {
		return fieldsX;
	}

	public int getFieldsY() {
		return fieldsY;
	}

	public double getGranularityX() {
		return granularityX;
	}

	public double getGranularityY() {
		return granularityY;
	}

	public int getCounterToPosition(int x, int y) {
		int xAbs = Math.abs(x);
		int yAbs = Math.abs(y);
		if (x == maxX)
			x--;
		if (y == maxY)
			y--;
		else if (yAbs > maxY || xAbs > maxX) {
			throw new ArrayIndexOutOfBoundsException();
		}
		return grid[(int) ((maxX + x) / granularityX)][(int) ((y + maxY) / granularityY)];
	}

	public int getXIndexForPosition(int x) {
		return (int) ((maxX + x) / granularityX);
	}

	public int getYIndexForPosition(int y) {
		return (int) ((y + maxY) / granularityY);
	}

	public GridCell getCellForPosition(int x, int y) {
		GridCell cell = new GridCell(this.getXIndexForPosition(x),
				this.getYIndexForPosition(y));
		return new GridCell(x, y);
	}
}
