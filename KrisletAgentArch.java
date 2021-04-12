import java.util.*;

import jason.architecture.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class KrisletAgentArch extends AgArch {
    @Override
    public void act(ActionExec action) {
        String a = action.getActionTerm().getFunctor();

        HashMap<String, String> commandToBeliefMap = new HashMap<String, String >(){{
            put("join_team", "team");  put("init_x", "init_x"); put("init_y", "init_y");
            put("station", "station");
        }};
        commandToBeliefMap.forEach((k,v) -> {
            if(k.equals(a)) {
                List<Term> newTerms = new LinkedList<Term>(){{
                    String s = getTS().getSettings().getUserParameter(v);
                    Arrays.asList(s.split(" ")).forEach(i -> {
                        add(ASSyntax.createLiteral(i));
                    });
                }};
                action.getActionTerm().setTerms(newTerms);
            }
        });
        super.act(action);
    }
}
