package vf.myapidoc.database.access.api.response.body

import utopia.vault.nosql.targeting.columns.{AccessManyColumns, HasValues}
import utopia.vault.nosql.targeting.many._
import utopia.vault.nosql.targeting.one.TargetingOne
import vf.myapidoc.database.access.api.node.group.FilterByApiNodeGroup
import vf.myapidoc.database.reader.api.ResponseContentTypeDbReader
import vf.myapidoc.database.storable.api._
import vf.myapidoc.model.stored.api.ResponseContentType

object AccessResponseContentTypes 
	extends DeprecatingWrapRowAccess[AccessResponseContentTypeRows](ResponseContentTypeDbModel) 
		with WrapOneToManyAccess[AccessCombinedResponseContentTypes] 
		with AccessManyDeprecatingRoot[AccessResponseContentTypeRows[ResponseContentType]]
{
	// ATTRIBUTES	--------------------
	
	override val all = apply(ResponseContentTypeDbReader).all
	
	
	// IMPLEMENTED	--------------------
	
	override def apply[A](access: TargetingMany[A]) = AccessCombinedResponseContentTypes(access)
	
	override protected def wrap[A](access: TargetingManyRows[A]) = AccessResponseContentTypeRows(access)
}

/**
  * Used for accessing multiple response content types from the DB at a time
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
abstract class AccessResponseContentTypes[A, +Repr <: TargetingManyLike[_, Repr, _]](wrapped: AccessManyColumns)
	extends TargetingTimeline[A, Repr, AccessResponseContentType[A]] 
		with HasValues[AccessResponseContentTypeValues] with FilterResponseContentTypes[Repr] 
		with TargetingManyLike[A, Repr, AccessResponseContentType[A]]
{
	// ATTRIBUTES	--------------------
	
	override lazy val values = AccessResponseContentTypeValues(wrapped)
	
	lazy val joinActiveStatuses = join(PossibleResponseStatusDbModel.toActiveJoin)
	lazy val joinActiveMethodImplementations = joinActiveStatuses.join(ApiNodeMethodImplementationDbModel.toActiveJoin)
	lazy val joinActiveNodes = joinActiveMethodImplementations.join(ApiNodeDbModel.toActiveJoin)
	
	lazy val joinActiveNodeGroups = joinActiveNodes.join(ApiNodeGroupDbModel.toActiveJoin)
	lazy val whereActiveNodeGroups = FilterByApiNodeGroup(joinActiveNodeGroups)
}

/**
  * Provides access to row-specific response content type -like items
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessResponseContentTypeRows[A](wrapped: TargetingManyRows[A]) 
	extends AccessResponseContentTypes[A, AccessResponseContentTypeRows[A]](wrapped) 
		with AccessRowsWrapper[A, AccessResponseContentTypeRows[A], AccessResponseContentType[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingManyRows[A]) = AccessResponseContentTypeRows(newTarget)
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = 
		AccessResponseContentType(target)
}

/**
  * Used for accessing response content type items that have been combined with one-to-many 
  * combinations
  * @param wrapped The wrapped access point
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
case class AccessCombinedResponseContentTypes[A](wrapped: TargetingMany[A]) 
	extends AccessResponseContentTypes[A, AccessCombinedResponseContentTypes[A]](wrapped) 
		with AccessWrapper[A, AccessCombinedResponseContentTypes[A], AccessResponseContentType[A]]
{
	// IMPLEMENTED	--------------------
	
	override def self = this
	
	override protected def wrap(newTarget: TargetingMany[A]) = AccessCombinedResponseContentTypes(newTarget)
	override protected def wrapUniqueTarget(target: TargetingOne[Option[A]]) = 
		AccessResponseContentType(target)
}

