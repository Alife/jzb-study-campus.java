/**
 * 
 */
package com.jzb.bc.model;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;

import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
public class SCN_Ope extends BKSNode {

    private String m_scenarioID;

    public static void resolveScenarioOpeRefs() {

        ArrayList<BKSNode> nodes = NodeRegistry.getByType(NodeType.SCN_Ope);
        ArrayList<BKSNode> toDelete = new ArrayList<BKSNode>();
        for (BKSNode node : nodes) {
            BKSNode refOpe = NodeRegistry.getByUID(node.getUID());
            if (refOpe == null) {
                Tracer._warn("Operation referenced in Scenario not found: " + node);
            } else {
                SCN_Ope sOpe = (SCN_Ope) node;
                if (refOpe.getType() == NodeType.P_Ope) {
                    sOpe.getScenario().getReferences().add(refOpe);
                    refOpe.getReferees().add(sOpe.getScenario());
                }
                toDelete.add(node);
            }
        }
        NodeRegistry.removeAll(toDelete);
    }

    private static String calcUID(Scenario scenario, String opName, String alName) {
        return "SCN_" + scenario.getSName() + "#" + alName + "#" + opName;
    }

    public SCN_Ope() {
    }

    public SCN_Ope(Scenario scenario, String opName, String alName) {
        super(NodeType.SCN_Ope, calcUID(scenario, opName, alName), opName, calcUID(scenario, opName, alName), null, true);
        m_scenarioID = scenario.getUID();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);

        m_scenarioID = in.readUTF();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);

        out.writeUTF(m_scenarioID);
    }

    private Scenario getScenario() {
        return (Scenario) NodeRegistry.getByUID(m_scenarioID);
    }
}
