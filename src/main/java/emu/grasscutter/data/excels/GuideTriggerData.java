package emu.grasscutter.data.excels;

import emu.grasscutter.data.*;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@ResourceType(name = "GuideTriggerExcelConfigData.json")
public class GuideTriggerData extends GameResource {
    // more like open state guide than quest guide
    private String guideName;
    private String type;
    private String openState;

    /**
     * The table has no id of its own - these are looked up by name, through the string map filled in
     * below. Standing in for one with the name's hash keeps every row in the id map too, rather than
     * piling all ninety onto key zero and keeping whichever landed last.
     */
    @Override
    public int getId() {
        return this.guideName == null ? 0 : this.guideName.hashCode();
    }

    public void onLoad() {
        GameData.getGuideTriggerDataStringMap().put(getGuideName(), this);
    }
}
