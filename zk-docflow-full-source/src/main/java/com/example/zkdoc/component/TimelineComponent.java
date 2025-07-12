package com.example.zkdoc.component;

import com.example.zkdoc.model.DocumentHistory;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;

public class TimelineComponent extends Div {
    private java.util.List<DocumentHistory> history;

    public void setHistory(java.util.List<DocumentHistory> history) {
        this.history = history;
        render();
    }

    private void render() {
        this.getChildren().clear();
        for (DocumentHistory h : history) {
            Div item = new Div();
            item.setSclass("timeline-item");
            Label label = new Label(h.getAction() + " by User " + h.getUserId() + " at " + h.getActionDate());
            if (h.getComments() != null) {
                label.setValue(label.getValue() + " (" + h.getComments() + ")");
            }
            item.appendChild(label);
            this.appendChild(item);
        }
    }

}