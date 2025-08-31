import React, { useState, useEffect } from 'react';
import { useAuth } from '../../hooks/useAuth';
import { Card, Button, Input, Table, Modal } from '../../components/ui';
import { Search, Edit, Trash2, Key, CheckCircle, XCircle, Shield, Plus, MoreHorizontal } from 'lucide-react';
import { toast } from 'react-hot-toast';
import { userService } from '../../services/userService';
import type { UserDTO, PasswordResetDTO, EmailUpdateDTO, RoleUpdateDTO } from '../../services/userService';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';

const UserManagementPage: React.FC = () => {
  const { isAdmin } = useAuth();
  const [searchTerm, setSearchTerm] = useState('');
  const [isPasswordResetModalOpen, setIsPasswordResetModalOpen] = useState(false);
  const [isEmailUpdateModalOpen, setIsEmailUpdateModalOpen] = useState(false);
  const [isRoleUpdateModalOpen, setIsRoleUpdateModalOpen] = useState(false);
  const [selectedUser, setSelectedUser] = useState<UserDTO | null>(null);
  const [newPassword, setNewPassword] = useState('');
  const [newEmail, setNewEmail] = useState('');
  const [selectedRoles, setSelectedRoles] = useState<string[]>([]);
  const [isCreateUserModalOpen, setIsCreateUserModalOpen] = useState(false);
  const [newUser, setNewUser] = useState({ username: '', email: '', password: '', roles: [] as string[] });
  const [isDeleteConfirmModalOpen, setIsDeleteConfirmModalOpen] = useState(false);
  
  const queryClient = useQueryClient();
  
  const { data: users, isLoading: loading } = useQuery({
    queryKey: ['users'],
    queryFn: userService.getAllUsers
  });

  const resetPasswordMutation = useMutation({
    mutationFn: userService.resetPassword,
    onSuccess: () => {
      toast.success('Password reset successfully');
      setIsPasswordResetModalOpen(false);
      setSelectedUser(null);
      setNewPassword('');
      queryClient.invalidateQueries({ queryKey: ['users'] });
    },
    onError: () => {
      toast.error('Failed to reset password');
    }
  });
  
  const updateEmailMutation = useMutation({
    mutationFn: userService.updateEmail,
    onSuccess: () => {
      toast.success('Email updated successfully');
      setIsEmailUpdateModalOpen(false);
      setSelectedUser(null);
      setNewEmail('');
      queryClient.invalidateQueries({ queryKey: ['users'] });
    },
    onError: () => {
      toast.error('Failed to update email');
    }
  });
  
  const updateRolesMutation = useMutation({
    mutationFn: userService.updateRoles,
    onSuccess: () => {
      toast.success('Roles updated successfully');
      setIsRoleUpdateModalOpen(false);
      setSelectedUser(null);
      setSelectedRoles([]);
      queryClient.invalidateQueries({ queryKey: ['users'] });
    },
    onError: () => {
      toast.error('Failed to update roles');
    }
  });
  
  const toggleStatusMutation = useMutation({
    mutationFn: userService.toggleUserStatus,
    onSuccess: () => {
      toast.success('User status updated successfully');
      queryClient.invalidateQueries({ queryKey: ['users'] });
    },
    onError: () => {
      toast.error('Failed to update user status');
    }
  });
  
  const createUserMutation = useMutation({
    mutationFn: userService.createUser,
    onSuccess: () => {
      toast.success('User created successfully');
      setIsCreateUserModalOpen(false);
      setNewUser({ username: '', email: '', password: '', roles: [] });
      queryClient.invalidateQueries({ queryKey: ['users'] });
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Failed to create user');
    }
  });

  const deleteUserMutation = useMutation({
    mutationFn: userService.deleteUser,
    onSuccess: () => {
      toast.success('User deleted successfully');
      setIsDeleteConfirmModalOpen(false);
      setSelectedUser(null);
      queryClient.invalidateQueries({ queryKey: ['users'] });
    },
    onError: () => {
      toast.error('Failed to delete user');
    }
  });
  
  const handleResetPassword = () => {
    if (!selectedUser || !newPassword) return;
    
    resetPasswordMutation.mutate({
      username: selectedUser.username,
      newPassword
    });
  };
  
  const handleUpdateEmail = () => {
    if (!selectedUser || !newEmail) return;
    
    updateEmailMutation.mutate({
      username: selectedUser.username,
      newEmail
    });
  };
  
  const handleUpdateRoles = () => {
    if (!selectedUser) return;
    
    console.log('Updating roles for user:', selectedUser.username);
    console.log('Selected roles:', selectedRoles);
    
    updateRolesMutation.mutate({
      username: selectedUser.username,
      roles: selectedRoles
    });
  };
  
  const handleToggleStatus = (username: string) => {
    toggleStatusMutation.mutate(username);
  };
  
  const handleCreateUser = () => {
    if (!newUser.username || !newUser.email || !newUser.password || newUser.roles.length === 0) {
      toast.error('Please fill all fields and select at least one role');
      return;
    }
    
    createUserMutation.mutate({
      username: newUser.username,
      email: newUser.email,
      password: newUser.password,
      roles: newUser.roles
    });
  };

  const handleDeleteUser = () => {
    if (!selectedUser) return;
    deleteUserMutation.mutate(selectedUser.username);
  };

  const filteredUsers = users?.filter(user => 
    user.username.toLowerCase().includes(searchTerm.toLowerCase()) ||
    user.email.toLowerCase().includes(searchTerm.toLowerCase())
  ) || [];

  // Debug logging
  console.log('Users data:', users);
  console.log('Filtered users:', filteredUsers);
  console.log('Loading state:', loading);

  return (
    <div className="p-4 md:p-6">
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-6 gap-4">
        <h1 className="text-xl md:text-2xl font-bold">User Management</h1>
        {isAdmin() && (
          <Button onClick={() => setIsCreateUserModalOpen(true)} className="w-full md:w-auto">
            <Plus className="w-4 h-4 mr-2" />
            Add User
          </Button>
        )}
      </div>

      <Card className="mb-6">
        <div className="p-4">
          <Input
            placeholder="Search users..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
      </Card>

      <Card>
        <div className="p-4 md:p-6">
          <h2 className="text-lg font-semibold mb-4">User Accounts</h2>
          <p className="mb-4 text-sm md:text-base">This section allows administrators to manage user accounts, reset passwords, and update email addresses.</p>
          
          {/* Table View with Horizontal Scroll */}
          <div className="overflow-x-auto">
            <Table>
              <Table.Header>
                <Table.Row>
                  <Table.HeaderCell className="min-w-[120px]">Username</Table.HeaderCell>
                  <Table.HeaderCell className="min-w-[200px]">Email</Table.HeaderCell>
                  <Table.HeaderCell className="min-w-[150px]">Roles</Table.HeaderCell>
                  <Table.HeaderCell className="min-w-[100px]">Status</Table.HeaderCell>
                  <Table.HeaderCell className="min-w-[200px]">Actions</Table.HeaderCell>
                </Table.Row>
              </Table.Header>
              <Table.Body>
                {loading ? (
                  <Table.Row>
                    <Table.Cell colSpan={5} className="text-center py-4">Loading...</Table.Cell>
                  </Table.Row>
                ) : filteredUsers.map((user) => (
                  <Table.Row key={user.id}>
                    <Table.Cell className="font-medium">{user.username}</Table.Cell>
                    <Table.Cell className="break-all">{user.email}</Table.Cell>
                    <Table.Cell>
                      <div className="flex flex-wrap gap-1">
                        {Array.isArray(user.roles) ? 
                          user.roles.map((role, index) => (
                            <span key={index} className="px-2 py-1 bg-blue-100 text-blue-800 text-xs rounded">
                              {typeof role === 'string' ? role.replace('ROLE_', '') : 
                               (role as any)?.name ? (role as any).name.replace('ROLE_', '') : 'Unknown'}
                            </span>
                          )) : 
                          <span className="text-gray-500">No roles</span>
                        }
                      </div>
                    </Table.Cell>
                    <Table.Cell>
                      {user.enabled ? (
                        <span className="flex items-center text-green-600">
                          <CheckCircle className="w-4 h-4 mr-1" /> Active
                        </span>
                      ) : (
                        <span className="flex items-center text-red-600">
                          <XCircle className="w-4 h-4 mr-1" /> Inactive
                        </span>
                      )}
                    </Table.Cell>
                    <Table.Cell>
                      {isAdmin() ? (
                        <div className="flex flex-wrap gap-1">
                          <Button
                            variant="outline"
                            size="sm"
                            onClick={() => {
                              setSelectedUser(user);
                              setIsPasswordResetModalOpen(true);
                            }}
                            title="Reset Password"
                          >
                            <Key className="w-4 h-4" />
                          </Button>
                          <Button
                            variant="outline"
                            size="sm"
                            onClick={() => {
                              setSelectedUser(user);
                              setNewEmail(user.email);
                              setIsEmailUpdateModalOpen(true);
                            }}
                            title="Edit Email"
                          >
                            <Edit className="w-4 h-4" />
                          </Button>
                          <Button
                            variant="outline"
                            size="sm"
                            onClick={() => {
                              setSelectedUser(user);
                              // Extract role names
                              const roleNames = [];
                              if (Array.isArray(user.roles)) {
                                for (const role of user.roles) {
                                  if (typeof role === 'string') {
                                    roleNames.push(role);
                                  } else if (role && (role as any).name) {
                                    roleNames.push((role as any).name);
                                  }
                                }
                              }
                              setSelectedRoles(roleNames);
                              setIsRoleUpdateModalOpen(true);
                            }}
                            title="Manage Roles"
                          >
                            <Shield className="w-4 h-4" />
                          </Button>
                          <Button
                            variant="outline"
                            size="sm"
                            onClick={() => handleToggleStatus(user.username)}
                            title={user.enabled ? "Deactivate User" : "Activate User"}
                          >
                            {user.enabled ? <XCircle className="w-4 h-4" /> : <CheckCircle className="w-4 h-4" />}
                          </Button>
                          <Button
                            variant="outline"
                            size="sm"
                            onClick={() => {
                              setSelectedUser(user);
                              setIsDeleteConfirmModalOpen(true);
                            }}
                            title="Delete User"
                            className="text-red-600 hover:text-red-700 border-red-300 hover:border-red-400"
                          >
                            <Trash2 className="w-4 h-4" />
                          </Button>
                        </div>
                      ) : (
                        <span className="text-gray-500 text-sm">No permissions</span>
                      )}
                    </Table.Cell>
                  </Table.Row>
                ))}
                {!loading && filteredUsers.length === 0 && (
                  <Table.Row>
                    <td colSpan={5} className="text-center py-4">No users found</td>
                  </Table.Row>
                )}
              </Table.Body>
            </Table>
          </div>
        </div>
      </Card>

      {/* Password Reset Modal */}
      <Modal
        isOpen={isPasswordResetModalOpen}
        onClose={() => {
          setIsPasswordResetModalOpen(false);
          setSelectedUser(null);
          setNewPassword('');
        }}
        title="Reset Password"
      >
        <div className="space-y-4">
          <p>Reset password for user: <strong>{selectedUser?.username}</strong></p>
          <Input
            type="password"
            label="New Password"
            value={newPassword}
            onChange={(e) => setNewPassword(e.target.value)}
            placeholder="Enter new password"
          />
          <div className="flex justify-end space-x-3">
            <Button
              variant="outline"
              onClick={() => {
                setIsPasswordResetModalOpen(false);
                setSelectedUser(null);
                setNewPassword('');
              }}
            >
              Cancel
            </Button>
            <Button
              onClick={handleResetPassword}
              disabled={!newPassword}
            >
              Reset Password
            </Button>
          </div>
        </div>
      </Modal>

      {/* Email Update Modal */}
      <Modal
        isOpen={isEmailUpdateModalOpen}
        onClose={() => {
          setIsEmailUpdateModalOpen(false);
          setSelectedUser(null);
          setNewEmail('');
        }}
        title="Update Email"
      >
        <div className="space-y-4">
          <p>Update email for user: <strong>{selectedUser?.username}</strong></p>
          <Input
            type="email"
            label="New Email"
            value={newEmail}
            onChange={(e) => setNewEmail(e.target.value)}
            placeholder="Enter new email"
          />
          <div className="flex justify-end space-x-3">
            <Button
              variant="outline"
              onClick={() => {
                setIsEmailUpdateModalOpen(false);
                setSelectedUser(null);
                setNewEmail('');
              }}
            >
              Cancel
            </Button>
            <Button
              onClick={handleUpdateEmail}
              disabled={!newEmail}
            >
              Update Email
            </Button>
          </div>
        </div>
      </Modal>

      {/* Role Update Modal */}
      <Modal
        isOpen={isRoleUpdateModalOpen}
        onClose={() => {
          setIsRoleUpdateModalOpen(false);
          setSelectedUser(null);
          setSelectedRoles([]);
        }}
        title="Update Roles"
      >
        <div className="space-y-4">
          <p>Update roles for user: <strong>{selectedUser?.username}</strong></p>
          <div className="space-y-2">
            <label className="block text-sm font-medium">Roles</label>
            <div className="space-y-2">
              {['ROLE_ADMIN', 'ROLE_CLERK', 'ROLE_TEACHER', 'ROLE_CLASS_TEACHER', 'ROLE_USER'].map((role) => (
                <div key={role} className="flex items-center">
                  <input
                    type="checkbox"
                    id={role}
                    checked={selectedRoles?.includes(role) || false}
                    onChange={(e) => {
                      if (e.target.checked) {
                        setSelectedRoles([...selectedRoles, role]);
                      } else {
                        setSelectedRoles(selectedRoles.filter(r => r !== role));
                      }
                    }}
                    className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
                  />
                  <label htmlFor={role} className="ml-2 block text-sm text-gray-900">
                    {role.replace('ROLE_', '')}
                  </label>
                </div>
              ))}
            </div>
          </div>
          <div className="flex justify-end space-x-3">
            <Button
              variant="outline"
              onClick={() => {
                setIsRoleUpdateModalOpen(false);
                setSelectedUser(null);
                setSelectedRoles([]);
              }}
            >
              Cancel
            </Button>
            <Button
              onClick={handleUpdateRoles}
              disabled={selectedRoles.length === 0}
            >
              Update Roles
            </Button>
          </div>
        </div>
      </Modal>

      {/* Create User Modal */}
      <Modal
        isOpen={isCreateUserModalOpen}
        onClose={() => {
          setIsCreateUserModalOpen(false);
          setNewUser({ username: '', email: '', password: '', roles: [] });
        }}
        title="Create New User"
      >
        <div className="space-y-4">
          <Input
            label="Username"
            value={newUser.username}
            onChange={(e) => setNewUser({ ...newUser, username: e.target.value })}
            placeholder="Enter username"
          />
          <Input
            type="email"
            label="Email"
            value={newUser.email}
            onChange={(e) => setNewUser({ ...newUser, email: e.target.value })}
            placeholder="Enter email"
          />
          <Input
            type="password"
            label="Password"
            value={newUser.password}
            onChange={(e) => setNewUser({ ...newUser, password: e.target.value })}
            placeholder="Enter password"
          />
          <div className="space-y-2">
            <label className="block text-sm font-medium">Roles</label>
            <div className="space-y-2">
              {['ADMIN', 'CLERK', 'TEACHER', 'CLASS_TEACHER'].map((role) => (
                <div key={role} className="flex items-center">
                  <input
                    type="checkbox"
                    id={`new-${role}`}
                    checked={newUser.roles.includes(role)}
                    onChange={(e) => {
                      if (e.target.checked) {
                        setNewUser({ ...newUser, roles: [...newUser.roles, role] });
                      } else {
                        setNewUser({ ...newUser, roles: newUser.roles.filter(r => r !== role) });
                      }
                    }}
                    className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
                  />
                  <label htmlFor={`new-${role}`} className="ml-2 block text-sm text-gray-900">
                    {role.replace('_', ' ')}
                  </label>
                </div>
              ))}
            </div>
          </div>
          <div className="flex justify-end space-x-3">
            <Button
              variant="outline"
              onClick={() => {
                setIsCreateUserModalOpen(false);
                setNewUser({ username: '', email: '', password: '', roles: [] });
              }}
            >
              Cancel
            </Button>
            <Button
              onClick={handleCreateUser}
              disabled={!newUser.username || !newUser.email || !newUser.password || newUser.roles.length === 0}
              loading={createUserMutation.isPending}
            >
              Create User
            </Button>
          </div>
        </div>
      </Modal>

      {/* Delete Confirmation Modal */}
      <Modal
        isOpen={isDeleteConfirmModalOpen}
        onClose={() => {
          setIsDeleteConfirmModalOpen(false);
          setSelectedUser(null);
        }}
        title="Confirm Deletion"
      >
        <div className="space-y-4">
          <p>Are you sure you want to delete user <strong>{selectedUser?.username}</strong>? This action cannot be undone.</p>
          <div className="flex justify-end space-x-3">
            <Button
              variant="outline"
              onClick={() => {
                setIsDeleteConfirmModalOpen(false);
                setSelectedUser(null);
              }}
            >
              Cancel
            </Button>
            <Button
              onClick={handleDeleteUser}
              loading={deleteUserMutation.isPending}
              className="text-red-600"
            >
              Delete User
            </Button>
          </div>
        </div>
      </Modal>
    </div>
  );
};

export default UserManagementPage;