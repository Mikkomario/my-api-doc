package vf.myapidoc.database.access.test.file

import utopia.vault.nosql.targeting.columns.{AccessManyColumns, HasValues}
import utopia.vault.nosql.targeting.many.{AccessManyRoot, AccessRowsWrapper, AccessWrapper, TargetingMany, TargetingManyLike, TargetingManyRows, WrapOneToManyAccess, WrapRowAccess}
import utopia.vault.nosql.targeting.one.TargetingOne
import vf.myapidoc.database.reader.test.TestsFileDbReader
import vf.myapidoc.model.stored.test.TestsFile

object AccessTestFiles 
	extends WrapRowAccess[AccessTestsFileRows] with WrapOneToManyAccess[AccessCombinedTestFiles] 
		with AccessManyRoot[AccessTestsFileRows[TestsFile]]
{
	// ATTRIBUTES	--------------------
	
	override val root = apply(TestsFileDbReader)
	
	
	// IMPLEMENTED	--------------------
	
	override def apply[A](access: TargetingManyRows[A]) = AccessTestsFileRows(access)
	
	override def apply[A](access: TargetingMany[A]) = AccessCombinedTestFiles(access)
}

/**
  * Used for accessing multiple test files from the DB at a time
  * @author Mikko Hilpinen
  * @since 01.11.2026, v0.1
  */
abstract class AccessTestFiles[A, +Repr <: TargetingManyLike[_, Repr, _]](wrapped: AccessManyColumns) 
	extends TargetingManyLike[A, Repr, AccessTestsFile[A]] with HasValues[AccessTestsFileValues] 
		with FilterTestFiles[Repr]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessTestsFileValues(wrapped)
}

/**
  * Provides access to row-specific tests file -like items
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 01.11.2026, v0.1
  */
case class AccessTestsFileRows[A](wrapped: TargetingManyRows[A]) 
	extends AccessTestFiles[A, AccessTestsFileRows[A]](wrapped) 
		with AccessRowsWrapper[A, AccessTestsFileRows[A], AccessTestsFile[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingManyRows[A]) = AccessTestsFileRows(newTarget)
	
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = AccessTestsFile(target)
}

/**
  * Used for accessing tests file items that have been combined with one-to-many combinations
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 01.11.2026, v0.1
  */
case class AccessCombinedTestFiles[A](wrapped: TargetingMany[A]) 
	extends AccessTestFiles[A, AccessCombinedTestFiles[A]](wrapped) 
		with AccessWrapper[A, AccessCombinedTestFiles[A], AccessTestsFile[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingMany[A]) = AccessCombinedTestFiles(newTarget)
	
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = AccessTestsFile(target)
}

