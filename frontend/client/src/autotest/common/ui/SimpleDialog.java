package autotest.common.ui;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;

/**
 * Simple DialogBox subclass that displays a title, contents, and an OK button to close the dialog.
 *
 */
public class SimpleDialog extends DialogBox {
    public SimpleDialog(String title, Widget contents) {
        super(false, false);
        
        FlexTable flex = new FlexTable();
        flex.setText(0, 0, title);
        flex.getFlexCellFormatter().setStylePrimaryName(0, 0, "field-name");
        
        flex.setWidget(1, 0, contents);
        
        Button ok = new Button("OK");
        ok.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                hide();
            }
        });
        flex.setWidget(2, 0, ok);
        
        add(flex);
    }
}