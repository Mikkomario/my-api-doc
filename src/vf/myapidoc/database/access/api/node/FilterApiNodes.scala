package vf.myapidoc.database.access.api.node

import utopia.flow.generic.casting.ValueConversions._
import utopia.vault.model.immutable.Column
import utopia.vault.nosql.template.IntIndexFilterable
import utopia.vault.nosql.view.{NullDeprecatableView, TimelineView}
import utopia.vault.sql.Condition
import vf.myapidoc.database.storable.api.ApiNodeDbModel

/**
  * Common trait for access points which may be filtered based on api node properties
  * @author Mikko Hilpinen
  * @since 15.10.2026, v0.1
  */
trait FilterApiNodes[+Repr] extends NullDeprecatableView[Repr] with TimelineView[Repr] with IntIndexFilterable[Repr]
{
	// COMPUTED	--------------------
	
	/**
	  * Model that defines api node database properties
	  */
	def model = ApiNodeDbModel
	
	
	// IMPLEMENTED	--------------------
	
	override protected def idColumn: Column = model.id
	
	override def timestampColumn = model.created
	
	
	// OTHER	--------------------
	
	/**
	  * @param groupId group id to target
	  * @return Copy of this access point that only includes api nodes with the specified group id
	  */
	def inGroup(groupId: Int) = filter(model.groupId.column <=> groupId)
	/**
	  * @param groupIds Targeted group ids
	  * @return Copy of this access point that only includes api nodes where group id is within the specified 
	  * value set
	  */
	def inGroups(groupIds: IterableOnce[Int]) = filter(Condition.indexIn(model.groupId, groupIds))
	
	/**
	  * @param parentId parent id to target
	  * @return Copy of this access point that only includes api nodes with the specified parent id
	  */
	def underParent(parentId: Int) = filter(model.parentId.column <=> parentId)
	/**
	  * @param parentIds Targeted parent ids
	  * @return Copy of this access point that only includes api nodes where parent id is within the 
	  * specified value set
	  */
	def underParents(parentIds: IterableOnce[Int]) = filter(Condition.indexIn(model.parentId, parentIds))
	
	/**
	  * @param nameNoSpaces name no spaces to target
	  * @return Copy of this access point that only includes api nodes with the specified name no spaces
	  */
	def withNameMatching(nameNoSpaces: String) = filter(model.nameNoSpaces.column <=> nameNoSpaces)
	/**
	  * @param namesNoSpaces Targeted names no spaces
	  * @return Copy of this access point that only includes api nodes where name no spaces is within the 
	  * specified value set
	  */
	def withNameMatchingAnyOf(namesNoSpaces: Iterable[String]) = 
		filter(model.nameNoSpaces.column.in(namesNoSpaces))
	
	/**
	  * @param relativePath relative path to target
	  * @return Copy of this access point that only includes api nodes with the specified relative path
	  */
	def withRelativePath(relativePath: String) = filter(model.relativePath.column <=> relativePath)
	/**
	  * @param relativePaths Targeted relative paths
	  * @return Copy of this access point that only includes api nodes where relative path is within the 
	  * specified value set
	  */
	def withRelativePaths(relativePaths: Iterable[String]) = 
		filter(model.relativePath.column.in(relativePaths))
}

