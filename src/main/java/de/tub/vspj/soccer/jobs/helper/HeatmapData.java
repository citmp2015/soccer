package de.tub.vspj.soccer.jobs.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tub.vspj.soccer.models.Field;
import de.tub.vspj.soccer.models.fieldobjects.Goalkeeper;
import de.tub.vspj.soccer.models.fieldobjects.IFieldObject;
import de.tub.vspj.soccer.models.fieldobjects.Player;

/**
 * Helper class to represent data for the heatmap generation
 */
public class HeatmapData {

    public static Logger LOG = LoggerFactory.getLogger(HeatmapData.class);

    // Possible resolution values for the field based on the project description
    // 8 X 13 --> 104 cells
    // 16 X 25 --> 400 cells
    // 32 X 50 --> 1600 cells
    // 64 X 100 --> 6400 cells
    public static int fieldXResolution = 16;
    public static int fieldYResolution = 25;

    // Used in the addPoint method. Length and Width of a specific square in the final heatmap.
    // Compute here only once and not every method call
    private static int sectorsWidth = Field.WIDTH / fieldXResolution;
    private static int sectorsLength = Field.LENGTH / fieldYResolution;

    public IFieldObject owner;
    public int[][] field = new int[fieldXResolution][fieldYResolution];

    @Override
    public String toString() {
        StringBuffer str = new StringBuffer();

        str.append("Heatmap data for ");
        if (owner.objectType() == IFieldObject.ObjectType.Player) {
            Player player = (Player) owner;
            str.append("Player [");
            str.append(player.team().name());
            str.append("] ");
            str.append(player.name());

            if (player instanceof Goalkeeper)
                str.append(" (GK)");
        } else if (owner.objectType() == IFieldObject.ObjectType.Ball) {
            str.append("[Ball]");
        } else if (owner.objectType() == IFieldObject.ObjectType.Referee) {
            str.append("[Referee]");
        }

        for (int i = 0; i < fieldYResolution; i++) {
            str.append("\n[");
            for (int j = 0; j < fieldXResolution; j++) {
                str.append(field[j][i]);
                str.append(", ");
            }
            str.append("]");
        }

        return str.toString();
    }

    // Suggested values for approximated field by project description
    // (0,33965), (0,-33960),(52483,33965),(52483,-33960)
    public void addPoint(int x, int y) {

        // LOG.info("Got points ({},{}). Width: {}, Height: {}", x, y, sectorsWidth, sectorsLength);

        // Add size of half field to y coordinate to avoid negative values
        y += 33965;

        // If measurement is out of field, do not take into account
        if (x < 0 || x > 52483 || y < 0 || y > 67925)
            return;

        // No floor function needed, because it is implicit in java integer division
        int xField = x / sectorsWidth;
        int yField = y / sectorsLength;

        field[xField][yField] += 1;
    }
}