package vf.myapidoc.database.access.test.file

import utopia.vault.nosql.targeting.columns.HasValues
import utopia.vault.nosql.targeting.one.{AccessOneRoot, AccessOneWrapper, TargetingOne}
import vf.myapidoc.model.stored.test.TestsFile

object AccessTestsFile extends AccessOneRoot[AccessTestsFile[TestsFile]]
{
	// ATTRIBUTES	--------------------
	
	override val root = AccessTestFiles.root.head
}

/**
  * Used for accessing individual test files from the DB at a time
  * @author Mikko Hilpinen
  * @since 01.11.2026, v0.1
  */
case class AccessTestsFile[A](wrapped: TargetingOne[Option[A]]) 
	extends AccessOneWrapper[Option[A], AccessTestsFile[A]] with HasValues[AccessTestsFileValue] 
		with FilterTestFiles[AccessTestsFile[A]]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessTestsFileValue(wrapped)
	
	
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingOne[Option[A]]) = AccessTestsFile(newTarget)
}

