package de.tu_berlin.cit.vs15.soccer;

public class GridCell {
	private int idX, idY;

	public GridCell(int x, int y) {
		this.idX = x;
		this.idY = y;
	}

	@Override
	public String toString() {
		return "Cell[" + idX + "][" + idY + "]";
	}

	public int getIdX() {
		return this.idX;
	}

	public int getIdY() {
		return this.idY;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof GridCell) {
			GridCell cell = (GridCell) o;
			if (cell.getIdX() == this.idX && cell.getIdY() == this.idY)
				return true;
		}
		return false;
	}
}
