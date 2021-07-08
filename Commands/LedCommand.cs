using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Text;
using System.Windows.Input;
using WpfDesktopApplicationv2.Stores;

namespace WpfDesktopApplicationv2.Commands
{
    public class LedCommand : ICommand
    {
        // coordinates 
        private int _x, _y;

        // mediator
        private readonly BroadcastLedSelectedStore _broadcast;

        /// <summary>
        /// Initialize Command for led button.
        /// </summary>
        /// <param name="x">X coordinate</param>
        /// <param name="y">Y coordinate</param>
        /// <param name="broadcast">Mediator class for broadcasting coordinates of LED</param>
        public LedCommand(int x, int y, BroadcastLedSelectedStore broadcast)
        {
            _x = x;
            _y = y;
            _broadcast = broadcast;
        }

        public event EventHandler CanExecuteChanged;

        public bool CanExecute(object parameter)
        {
            return true;
        }

        public void Execute(object parameter)
        {
            Debug.WriteLine("Led button clicked: " + _x + ", " + _y);
            _broadcast.OnLedSelected(new KeyValuePair<int, int>(_x, _y));
        }
    }
}
