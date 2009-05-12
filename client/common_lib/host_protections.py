from autotest_lib.client.common_lib import enum, global_config

# Changing this file has consequences that need to be understood.
# Adding a protection level to the enum requires you to append your change to
# the end of the enum or a database migration needs to be added to migrate
# older protections to match the layout of the new enum.
# Removing a protection level from the enum requires a database migration to
# update the integer values in the DB and migrate hosts that use the removed
# protection to a default protection level.
# IF THIS IS NOT DONE HOSTS' PROTECTION LEVELS WILL BE CHANGED RANDOMLY.

Protection = enum.Enum('No protection',          # Repair can do anything to
                                                 # this host.
                       'Repair software only',   # repair should try to fix any
                                                 # software problem
                       'Repair filesystem only', # Repair should only try to
                                                 # recover the file system.
                       'Do not repair',          # Repair should not touch this
                                                 # host.
                       'Do not verify',          # Don't even try to verify
                                                 # this host
                       )

default = Protection.get_value(
    global_config.global_config.get_config_value(
        'HOSTS', 'default_protection'))

choices = Protection.choices()
