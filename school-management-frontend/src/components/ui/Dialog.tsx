import React, { createContext, useContext, useState, ReactNode } from 'react';
import { X } from 'lucide-react';
import { cn } from '../../utils';

interface DialogContextType {
  open: boolean;
  onOpenChange: (open: boolean) => void;
}

const DialogContext = createContext<DialogContextType | undefined>(undefined);

const useDialogContext = () => {
  const context = useContext(DialogContext);
  if (!context) {
    throw new Error('Dialog components must be used within a Dialog');
  }
  return context;
};

interface DialogProps {
  open?: boolean;
  onOpenChange?: (open: boolean) => void;
  children: ReactNode;
}

const Dialog: React.FC<DialogProps> = ({ open = false, onOpenChange, children }) => {
  const [internalOpen, setInternalOpen] = useState(false);
  
  const isOpen = open !== undefined ? open : internalOpen;
  const handleOpenChange = onOpenChange || setInternalOpen;

  return (
    <DialogContext.Provider value={{ open: isOpen, onOpenChange: handleOpenChange }}>
      {children}
    </DialogContext.Provider>
  );
};

interface DialogTriggerProps {
  asChild?: boolean;
  children: ReactNode;
}

const DialogTrigger: React.FC<DialogTriggerProps> = ({ asChild, children }) => {
  const { onOpenChange } = useDialogContext();
  
  if (asChild && React.isValidElement(children)) {
    return React.cloneElement(children, {
      onClick: () => onOpenChange(true),
    });
  }
  
  return (
    <button onClick={() => onOpenChange(true)}>
      {children}
    </button>
  );
};

interface DialogContentProps {
  children: ReactNode;
  className?: string;
}

const DialogContent: React.FC<DialogContentProps> = ({ children, className }) => {
  const { open, onOpenChange } = useDialogContext();

  if (!open) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center">
      {/* Backdrop */}
      <div 
        className="fixed inset-0 bg-black bg-opacity-50"
        onClick={() => onOpenChange(false)}
      />
      
      {/* Content */}
      <div className={cn(
        'relative bg-white rounded-lg shadow-lg max-w-lg w-full mx-4 max-h-[90vh] overflow-y-auto',
        className
      )}>
        <button
          onClick={() => onOpenChange(false)}
          className="absolute top-4 right-4 text-gray-400 hover:text-gray-600"
        >
          <X className="h-4 w-4" />
        </button>
        {children}
      </div>
    </div>
  );
};

interface DialogHeaderProps {
  children: ReactNode;
  className?: string;
}

const DialogHeader: React.FC<DialogHeaderProps> = ({ children, className }) => {
  return (
    <div className={cn('px-6 py-4 border-b border-gray-200', className)}>
      {children}
    </div>
  );
};

interface DialogTitleProps {
  children: ReactNode;
  className?: string;
}

const DialogTitle: React.FC<DialogTitleProps> = ({ children, className }) => {
  return (
    <h2 className={cn('text-lg font-semibold text-gray-900', className)}>
      {children}
    </h2>
  );
};

interface DialogFooterProps {
  children: ReactNode;
  className?: string;
}

const DialogFooter: React.FC<DialogFooterProps> = ({ children, className }) => {
  return (
    <div className={cn('px-6 py-4 border-t border-gray-200 flex justify-end space-x-2', className)}>
      {children}
    </div>
  );
};

export {
  Dialog,
  DialogTrigger,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
};
