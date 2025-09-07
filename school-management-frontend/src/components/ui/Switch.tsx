import React, { useState } from 'react';
import { cn } from '../../utils';

interface SwitchProps {
  checked?: boolean;
  onCheckedChange?: (checked: boolean) => void;
  disabled?: boolean;
  className?: string;
  id?: string;
}

const Switch: React.FC<SwitchProps> = ({
  checked = false,
  onCheckedChange,
  disabled = false,
  className,
  id,
  ...props
}) => {
  const [isChecked, setIsChecked] = useState(checked);

  const handleToggle = () => {
    if (disabled) return;
    
    const newChecked = !isChecked;
    setIsChecked(newChecked);
    onCheckedChange?.(newChecked);
  };

  return (
    <button
      type="button"
      role="switch"
      aria-checked={isChecked}
      disabled={disabled}
      onClick={handleToggle}
      className={cn(
        'relative inline-flex h-6 w-11 items-center rounded-full transition-colors focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2',
        isChecked ? 'bg-blue-600' : 'bg-gray-200',
        disabled && 'opacity-50 cursor-not-allowed',
        className
      )}
      id={id}
      {...props}
    >
      <span
        className={cn(
          'inline-block h-4 w-4 transform rounded-full bg-white transition-transform',
          isChecked ? 'translate-x-6' : 'translate-x-1'
        )}
      />
    </button>
  );
};

export default Switch;
