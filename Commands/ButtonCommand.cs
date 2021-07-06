using System;
using System.Collections.Generic;
using System.Text;

namespace WpfDesktopApplicationv2.Commands
{
    class ButtonCommand
    {
        private readonly Action _handler;
        private readonly bool _isEnabled;

        /// <summary>
        /// Constructor for Button Command. 
        /// </summary>
        /// <param name="handler">It is a function to be executed on button press</param>
        public ButtonCommand(Action handler)
        {
            _handler = handler;
            _isEnabled = true;
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
