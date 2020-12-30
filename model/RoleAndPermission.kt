
package io.collapp.model

import ch.digitalfondue.npjt.ConstructorAnnotationRowMapper.Column

class RoleAndPermission(@Column("ROLE_NAME") val roleName: String, @Column("ROLE_REMOVABLE") val removable: Boolean,
                        @Column("ROLE_HIDDEN") val hidden: Boolean, @Column("ROLE_READONLY") val readOnly: Boolean,
                        /** can be null  */
                        @Column("PERMISSION") val permission: Permission?) {

    /** if permission is null, category is null too  */
    val category: PermissionCategory?

    init {
        this.category = permission?.category
    }

    override fun equals(obj: Any?): Boolean {
        return if (obj != null && obj is RoleAndPermission)
            roleName == obj.roleName
        else
            false
    }

    override fun hashCode(): Int {
        return roleName.hashCode()
    }
}
