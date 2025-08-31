export const getImageUrl = (path: string | null | undefined): string | null => {
  if (!path) return null;
  if (path.startsWith('http')) return path;
  
  // Use the same base URL as the API (through nginx proxy)
  const baseUrl = process.env.NODE_ENV === 'production' 
    ? 'http://localhost:8080' 
    : 'http://localhost:8080';
    
  return `${baseUrl}${path}`;
};