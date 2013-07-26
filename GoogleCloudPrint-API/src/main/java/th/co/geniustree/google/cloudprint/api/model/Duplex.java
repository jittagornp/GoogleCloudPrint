/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.geniustree.google.cloudprint.api.model;

import java.util.List;

/**
 *
 * @author jittagorn pitakmetagoon
 */
public class Duplex {

    private List<DuplexOption> option;

    public List<DuplexOption> getOption() {
        return option;
    }

    public void setOption(List<DuplexOption> option) {
        this.option = option;
    }
}
