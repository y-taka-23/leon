package leon
package synthesis
package rules

import purescala.Trees._
import purescala.TypeTrees._
import purescala.TreeOps._
import purescala.Extractors._

object Unification {
  case object DecompTrivialClash extends Rule("Unif Dec./Clash/Triv.", 200) {
    def attemptToApplyOn(sctx: SynthesisContext, p: Problem): RuleResult = {
      val TopLevelAnds(exprs) = p.phi

      val (toRemove, toAdd) = exprs.collect {
        case eq @ Equals(cc1 @ CaseClass(cd1, args1), cc2 @ CaseClass(cd2, args2)) =>
          if (cc1 == cc2) {
            (eq, List(BooleanLiteral(true)))
          } else if (cd1 == cd2) {
            (eq, (args1 zip args2).map((Equals(_, _)).tupled))
          } else {
            (eq, List(BooleanLiteral(false)))
          }
      }.unzip

      if (!toRemove.isEmpty) {
        val sub = p.copy(phi = And((exprs.toSet -- toRemove ++ toAdd.flatten).toSeq))


        RuleFastStep(List(sub), forward)
      } else {
        RuleInapplicable
      }
    }
  }

  // This rule is probably useless; it never happens except in crafted
  // examples, and will be found by OptimisticGround anyway.
  case object OccursCheck extends Rule("Unif OccursCheck", 200) {
    def attemptToApplyOn(sctx: SynthesisContext, p: Problem): RuleResult = {
      val TopLevelAnds(exprs) = p.phi

      val isImpossible = exprs.exists {
        case eq @ Equals(cc : CaseClass, Variable(id)) if variablesOf(cc) contains id =>
          true
        case eq @ Equals(Variable(id), cc : CaseClass) if variablesOf(cc) contains id =>
          true
        case _ =>
          false
      }

      if (isImpossible) {
        val tpe = TupleType(p.xs.map(_.getType))

        RuleFastSuccess(Solution(BooleanLiteral(false), Set(), Error(p.phi+" is UNSAT!").setType(tpe)))
      } else {
        RuleInapplicable
      }
    }
  }
}

