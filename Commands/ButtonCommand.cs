using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Input;

namespace WpfDesktopApplicationv2.Commands
{
    class ButtonCommand:ICommand
    {
        private Action _handler;
        private bool _isEnabled;

        /// <summary>
        /// Constructor for Button Command. 
        /// </summary>
        /// <param name="handler">It is a function to be executed on button press</param>
        public ButtonCommand(Action handler)
        {
            _handler = handler;
            _isEnabled = true;
        }

        public bool IsEnabled
        {
            get { return _isEnabled; }
            set
            {
                if (value != _isEnabled)
                {
                    _isEnabled = value;
                    CanExecuteChanged?.Invoke(this, EventArgs.Empty);
                }
            }
        }

        public event EventHandler CanExecuteChanged;

        public bool CanExecute(object parameter)
        {
            return _isEnabled;
        }

        /// <summary>
        /// Execute an Action that was passed to the Button Command
        /// </summary>
        /// <param name="parameter"></param>
        public void Execute(object parameter)
        {
            _handler();
        }
    }
}
