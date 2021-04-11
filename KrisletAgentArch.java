import java.util.*;

import jason.architecture.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class KrisletAgentArch extends AgArch {
    @Override
    public void act(ActionExec action) {
        if (action.getActionTerm().getFunctor().equals("join_team")) {
            List<Term> newTerms = new LinkedList<Term>(){{
                add(ASSyntax.createLiteral(getTS().getSettings().getUserParameter("team")));
            }};
            action.getActionTerm().setTerms(newTerms);
        }
        super.act(action);
    }
}
