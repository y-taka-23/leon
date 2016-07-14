package leon
package laziness

import invariant.util._
import purescala.Definitions._
import purescala.Expressions._
import purescala.ExprOps._
import purescala.Extractors._
import HOMemUtil._
import ProgramUtil._
import invariant.datastructure._
import purescala.Types._
import purescala.TypeOps._
import TypeUtil._

/**
 * Performs type-level analysis to know which types have their targets
 * completely within scope.
 * TODO: we can extend this with more sophisticated control-flow analysis
 */
class FunctionTypeAnalysis(p: Program, funsManager: FunctionsManager) {

  def isPrivate(d: Definition) = {
    d match {
      case cd: ClassDef => cd.flags.contains(IsPrivate)
      case fd: FunDef   => fd.flags.contains(IsPrivate)
    }
  }
  val escapingTypes = p.units.filter(_.isMainUnit).flatMap { md =>
    // fields and parameters of public classes and methods are accessible from outside, others are not
    (md.definedClasses ++ md.definedFunctions).flatMap {
      case cd: ClassDef if !isPrivate(cd) =>
        //println(s"Types escaping due to public class ${cd.id}: ${cd.fields.map(_.getType)}")
        cd.typed +: cd.fields.map(_.getType)
      case fd: FunDef if !isPrivate(fd)   =>
        //println(s"Types escaping due to public fundef ${fd.id}: ${fd.params.map(_.getType)}")
        fd.params.map(_.getType)
      case _ => Seq()
    }
  }
  /**
   * A function type escapes if it is a **super type** of an escaping type
   */
  def isEscapingType(ft: FunctionType) = escapingTypes.exists{ escType => 
    //canBeSubtypeOf(ft, getTypeParameters(ft), escType).isDefined  }
    canBeSubtypeOf(escType, ft).isDefined }
}
