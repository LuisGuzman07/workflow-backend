package bo.edu.uagrm.backend.dto;

public class AiTaskInput {

    private String id;
    private String label;
    private String laneTitle;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLaneTitle() {
        return laneTitle;
    }

    public void setLaneTitle(String laneTitle) {
        this.laneTitle = laneTitle;
    }
}
