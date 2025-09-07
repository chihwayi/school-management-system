import React from 'react';
import { cn } from '../../utils';

interface LabelProps extends React.LabelHTMLAttributes<HTMLLabelElement> {
  children: React.ReactNode;
  className?: string;
}

const Label: React.FC<LabelProps> = ({ children, className, ...props }) => {
  return (
    <label
      className={cn(
        'text-sm font-medium text-gray-700 mb-1 block',
        className
      )}
      {...props}
    >
      {children}
    </label>
  );
};

export default Label;
