/**
 * Tenant detection utility for multi-tenant school system
 */

export const getCurrentTenant = () => {
  const hostname = window.location.hostname;
  
  // Extract subdomain from hostname
  if (hostname.includes('.localhost')) {
    const subdomain = hostname.split('.')[0];
    return subdomain !== 'localhost' ? subdomain : null;
  }
  
  // For production domains like school.yourdomain.com
  if (hostname.includes('.') && !hostname.startsWith('localhost')) {
    const parts = hostname.split('.');
    if (parts.length >= 3) {
      return parts[0]; // First part is subdomain
    }
  }
  
  // Check for tenant parameter in URL
  const urlParams = new URLSearchParams(window.location.search);
  const tenantParam = urlParams.get('tenant');
  if (tenantParam) {
    return tenantParam;
  }
  
  return null;
};

export const getTenantInfo = () => {
  const tenant = getCurrentTenant();
  return {
    tenant,
    isMultiTenant: !!tenant,
    baseUrl: tenant ? `http://${tenant}.localhost:3000` : 'http://localhost:3000'
  };
};

export const setTenant = (tenantId) => {
  // Store tenant in localStorage for persistence
  localStorage.setItem('selectedTenant', tenantId);
  
  // Redirect to tenant subdomain
  const currentUrl = window.location;
  const newUrl = `http://${tenantId}.localhost:3000${currentUrl.pathname}${currentUrl.search}`;
  window.location.href = newUrl;
};

export const redirectToTenant = (tenantId) => {
  const currentUrl = window.location;
  const newUrl = `http://${tenantId}.localhost:3000${currentUrl.pathname}${currentUrl.search}`;
  window.location.href = newUrl;
};