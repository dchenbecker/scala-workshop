package dung.eon

import org.specs._
import org.scalacheck._

class DungeonSpec extends Specification {
  "a fireball" should {
    "match" in {
      val fireball: Magic = Fire(Some(3), 3)
      fireball must beLike {
        case Fire(attack, range) => attack must beSome(3); range must be(3)
      }
    }
  }
}

class MapSpec extends Specification with ScalaCheck {
  import Gen._
  import Arbitrary.arbitrary
  implicit val arbLoc  = Arbitrary(for (name <- alphaStr suchThat (_.size > 3)) yield Location(name, Nil, Abandoned))
  implicit val arbLocs = Arbitrary(for (n <- choose(10, 50); locs <- Gen.listOfN(n, arbitrary[Location])) yield locs)

  "a randomly generated map" should {
    "be fully connected" in {
      val prop = Prop.forAll(
        (l: List[Location]) => {
          val map = DungeonMap.generate(l)
          l.forall(
            loc => map.exits(loc) must exist {
              case Exit(to, _) => map.exits(to) must exist {
                case Exit(from, _) => from == loc
              }
            }
          )
        }
      )

      prop must pass
    }
  }
}


// vim: set ts=4 sw=4 et:
