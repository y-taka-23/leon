package leon
package synthesis
package rules

import purescala.Trees._
import purescala.TreeOps._
import purescala.Extractors._

case object UnconstrainedOutput extends Rule("Unconstr.Output", 100) {
  def attemptToApplyOn(sctx: SynthesisContext, p: Problem): RuleResult = {
    val unconstr = p.xs.toSet -- variablesOf(p.phi)

    if (!unconstr.isEmpty) {
      val sub = p.copy(xs = p.xs.filterNot(unconstr))

      val onSuccess: List[Solution] => Solution = { 
        case List(s) =>
          Solution(s.pre, s.defs, LetTuple(sub.xs, s.term, Tuple(p.xs.map(id => if (unconstr(id)) simplestValue(Variable(id)) else Variable(id)))))
        case _ =>
          Solution.none
      }

      RuleFastStep(List(sub), onSuccess)
    } else {
      RuleInapplicable
    }

  }
}

