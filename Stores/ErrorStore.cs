using System;
using System.Collections.Generic;
using System.Text;

namespace WpfDesktopApplicationv2.Stores
{
    public class ErrorStore
    {
        public event Action Error;

        private string _errorState;

        public string ErrorState
        {
            get => _errorState;
            set
            {
                _errorState = value;
                OnError();
            }
        }

        public void OnError()
        {
            Error?.Invoke();
        }

    }
}
